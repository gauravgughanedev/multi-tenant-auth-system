-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tenant management table (in public schema)
CREATE TABLE IF NOT EXISTS public.tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

-- Create tenant-specific schemas
CREATE SCHEMA IF NOT EXISTS public;
CREATE SCHEMA IF NOT EXISTS tenant_acme;
CREATE SCHEMA IF NOT EXISTS tenant_beta;
CREATE SCHEMA IF NOT EXISTS tenant_gamma;

-- Create tenant-specific users table in each schema
DO $$
DECLARE
    schema_name TEXT;
    sql_cmd TEXT;
BEGIN
    FOR schema_name IN 
        SELECT 'tenant_' || tenant_id FROM public.tenants
        UNION SELECT 'tenant_acme' UNION SELECT 'tenant_beta' UNION SELECT 'tenant_gamma'
    LOOP
        -- Create users table in each tenant schema
        sql_cmd := format('
            CREATE TABLE IF NOT EXISTS %I.users (
                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                name VARCHAR(100) NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role VARCHAR(20) DEFAULT ''USER'' CHECK (role IN (''USER'', ''ADMIN'', ''SUPER_ADMIN'')),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP
            );
            
            -- Create indexes for performance
            CREATE INDEX IF NOT EXISTS idx_users_email ON %I.users(email);
            CREATE INDEX IF NOT EXISTS idx_users_role ON %I.users(role);
            CREATE INDEX IF NOT EXISTS idx_users_created_at ON %I.users(created_at);
        ', schema_name, schema_name, schema_name, schema_name);
        
        EXECUTE sql_cmd;
    END LOOP;
END $$;

-- Create tenant-specific sessions table
DO $$
DECLARE
    schema_name TEXT;
    sql_cmd TEXT;
BEGIN
    FOR schema_name IN 
        SELECT 'tenant_' || tenant_id FROM public.tenants
        UNION SELECT 'tenant_acme' UNION SELECT 'tenant_beta' UNION SELECT 'tenant_gamma'
    LOOP
        sql_cmd := format('
            CREATE TABLE IF NOT EXISTS %I.sessions (
                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                user_id UUID REFERENCES %I.users(id) ON DELETE CASCADE,
                token_hash VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at TIMESTAMP NOT NULL,
                ip_address INET,
                user_agent TEXT
            );
            
            CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON %I.sessions(user_id);
            CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON %I.sessions(expires_at);
        ', schema_name, schema_name, schema_name, schema_name);
        
        EXECUTE sql_cmd;
    END LOOP;
END $$;

-- Create tenant-specific audit logs table
DO $$
DECLARE
    schema_name TEXT;
    sql_cmd TEXT;
BEGIN
    FOR schema_name IN 
        SELECT 'tenant_' || tenant_id FROM public.tenants
        UNION SELECT 'tenant_acme' UNION SELECT 'tenant_beta' UNION SELECT 'tenant_gamma'
    LOOP
        sql_cmd := format('
            CREATE TABLE IF NOT EXISTS %I.audit_logs (
                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                user_id UUID REFERENCES %I.users(id),
                action VARCHAR(100) NOT NULL,
                resource_type VARCHAR(50) NOT NULL,
                resource_id UUID,
                old_values JSONB,
                new_values JSONB,
                ip_address INET,
                user_agent TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON %I.audit_logs(user_id);
            CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON %I.audit_logs(action);
            CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON %I.audit_logs(created_at);
        ', schema_name, schema_name, schema_name, schema_name);
        
        EXECUTE sql_cmd;
    END LOOP;
END $$;

-- Create a function to create tenant schema dynamically
CREATE OR REPLACE FUNCTION create_tenant_schema(p_tenant_id VARCHAR(50), p_name VARCHAR(100))
RETURNS UUID AS $$
DECLARE
    tenant_uuid UUID;
    schema_name TEXT;
    sql_cmd TEXT;
BEGIN
    -- Insert tenant record
    INSERT INTO public.tenants (tenant_id, name)
    VALUES (p_tenant_id, p_name)
    RETURNING id INTO tenant_uuid;
    
    -- Create schema
    schema_name := 'tenant_' || p_tenant_id;
    EXECUTE 'CREATE SCHEMA IF NOT EXISTS ' || quote_ident(schema_name);
    
    -- Create tables in new schema
    sql_cmd := format('
        CREATE TABLE IF NOT EXISTS %I.users (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            name VARCHAR(100) NOT NULL,
            email VARCHAR(255) UNIQUE NOT NULL,
            password_hash VARCHAR(255) NOT NULL,
            role VARCHAR(20) DEFAULT ''USER'' CHECK (role IN (''USER'', ''ADMIN'', ''SUPER_ADMIN'')),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            last_login TIMESTAMP
        );
        
        CREATE TABLE IF NOT EXISTS %I.sessions (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            user_id UUID REFERENCES %I.users(id) ON DELETE CASCADE,
            token_hash VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            expires_at TIMESTAMP NOT NULL,
            ip_address INET,
            user_agent TEXT
        );
        
        CREATE TABLE IF NOT EXISTS %I.audit_logs (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            user_id UUID REFERENCES %I.users(id),
            action VARCHAR(100) NOT NULL,
            resource_type VARCHAR(50) NOT NULL,
            resource_id UUID,
            old_values JSONB,
            new_values JSONB,
            ip_address INET,
            user_agent TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        
        -- Create indexes
        CREATE INDEX IF NOT EXISTS idx_users_email ON %I.users(email);
        CREATE INDEX IF NOT EXISTS idx_users_role ON %I.users(role);
        CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON %I.sessions(user_id);
        CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON %I.audit_logs(user_id);
    ', schema_name, schema_name, schema_name, schema_name, schema_name, schema_name, schema_name);
    
    EXECUTE sql_cmd;
    
    RETURN tenant_uuid;
END;
$$ LANGUAGE plpgsql;

-- Create a function to delete tenant schema
CREATE OR REPLACE FUNCTION delete_tenant_schema(p_tenant_id VARCHAR(50))
RETURNS VOID AS $$
DECLARE
    schema_name TEXT;
BEGIN
    -- Mark tenant as inactive
    UPDATE public.tenants 
    SET status = 'SUSPENDED' 
    WHERE tenant_id = p_tenant_id;
    
    -- Drop schema (this will cascade delete all tables)
    schema_name := 'tenant_' || p_tenant_id;
    EXECUTE 'DROP SCHEMA IF EXISTS ' || quote_ident(schema_name) || ' CASCADE';
END;
$$ LANGUAGE plpgsql;

-- Set default search path
ALTER DATABASE multitenant_auth SET search_path = public;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_acme TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_beta TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_gamma TO postgres;

-- Grant permissions on all tables in public schema
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Create triggers for updated_at timestamps
DO $$
DECLARE
    schema_name TEXT;
    sql_cmd TEXT;
BEGIN
    FOR schema_name IN 
        SELECT 'tenant_' || tenant_id FROM public.tenants
        UNION SELECT 'tenant_acme' UNION SELECT 'tenant_beta' UNION SELECT 'tenant_gamma'
    LOOP
        -- Create function for updated_at trigger
        sql_cmd := format('
            CREATE OR REPLACE FUNCTION %I.update_updated_at_column()
            RETURNS TRIGGER AS $$
            BEGIN
                NEW.updated_at = CURRENT_TIMESTAMP;
                RETURN NEW;
            END;
            $$ language ''plpgsql'';
            
            -- Create triggers for users table
            DROP TRIGGER IF EXISTS update_users_updated_at ON %I.users;
            CREATE TRIGGER update_users_updated_at 
                BEFORE UPDATE ON %I.users 
                FOR EACH ROW 
                EXECUTE FUNCTION %I.update_updated_at_column();
        ', schema_name, schema_name, schema_name, schema_name);
        
        EXECUTE sql_cmd;
    END LOOP;
END $$;