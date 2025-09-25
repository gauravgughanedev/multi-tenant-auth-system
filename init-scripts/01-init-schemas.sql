-- Create schemas for different tenants
CREATE SCHEMA IF NOT EXISTS public;
CREATE SCHEMA IF NOT EXISTS tenant_acme;
CREATE SCHEMA IF NOT EXISTS tenant_beta;
CREATE SCHEMA IF NOT EXISTS tenant_gamma;

-- Set default search path
ALTER DATABASE multitenant_auth SET search_path = public;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_acme TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_beta TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_gamma TO postgres;

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
