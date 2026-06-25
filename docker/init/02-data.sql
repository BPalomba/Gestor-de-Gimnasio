--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

-- Started on 2026-06-25 17:38:49

-- Deshabilitar restricciones FK durante la carga
SET session_replication_role = replica;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4894 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 33467)
-- Name: branches; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.branches (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    address text,
    city character varying(100),
    lat numeric(10,7),
    lng numeric(10,7),
    name character varying(150) NOT NULL,
    phone character varying(30),
    province character varying(100),
    gym_id uuid NOT NULL
);


ALTER TABLE public.branches OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 33474)
-- Name: gyms; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gyms (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    email character varying(255) NOT NULL,
    logo_url character varying(255),
    name character varying(150) NOT NULL,
    phone character varying(30),
    revenue_share_pct numeric(5,2),
    slug character varying(100) NOT NULL
);


ALTER TABLE public.gyms OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 33481)
-- Name: member_statuses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.member_statuses (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    active boolean NOT NULL,
    code character varying(30) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.member_statuses OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 33486)
-- Name: members; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.members (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    birth_date date,
    dni character varying(20),
    email character varying(255),
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    notes text,
    phone character varying(30),
    branch_id uuid,
    gym_id uuid NOT NULL,
    status_id uuid NOT NULL
);


ALTER TABLE public.members OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 33493)
-- Name: memberships; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.memberships (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    end_date date NOT NULL,
    frozen_days integer NOT NULL,
    frozen_since date,
    notes text,
    price_paid numeric(10,2) NOT NULL,
    start_date date NOT NULL,
    status character varying(20) NOT NULL,
    gym_id uuid NOT NULL,
    member_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    CONSTRAINT memberships_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'FROZEN'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.memberships OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 33501)
-- Name: payments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payments (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    amount numeric(10,2) NOT NULL,
    currency character varying(3) NOT NULL,
    notes text,
    payment_method character varying(20) NOT NULL,
    reference_code character varying(100),
    revenue_share_amount numeric(10,2),
    revenue_share_paid boolean NOT NULL,
    status character varying(20) NOT NULL,
    gym_id uuid NOT NULL,
    member_id uuid NOT NULL,
    membership_id uuid NOT NULL,
    registered_by uuid,
    CONSTRAINT payments_payment_method_check CHECK (((payment_method)::text = ANY ((ARRAY['CASH'::character varying, 'CARD'::character varying, 'TRANSFER'::character varying, 'MP'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT payments_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'COMPLETED'::character varying, 'REFUNDED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.payments OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 33510)
-- Name: permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.permissions (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    code character varying(50) NOT NULL,
    description character varying(255) NOT NULL,
    module character varying(30) NOT NULL
);


ALTER TABLE public.permissions OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 33515)
-- Name: plans; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.plans (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    currency character varying(3) NOT NULL,
    description text,
    duration_days integer NOT NULL,
    name character varying(150) NOT NULL,
    price numeric(10,2) NOT NULL,
    is_public boolean NOT NULL,
    sessions_per_week integer,
    gym_id uuid NOT NULL
);


ALTER TABLE public.plans OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 33522)
-- Name: role_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role_permissions (
    role_id uuid NOT NULL,
    permission_id uuid NOT NULL
);


ALTER TABLE public.role_permissions OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 33527)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    active boolean NOT NULL,
    description character varying(255),
    name character varying(100) NOT NULL,
    gym_id uuid NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 33532)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(100) NOT NULL,
    last_login_at timestamp(6) with time zone,
    last_name character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    branch_id uuid,
    gym_id uuid,
    role_id uuid NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 4878 (class 0 OID 33467)
-- Dependencies: 217
-- Data for Name: branches; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.branches (id, created_at, updated_at, is_active, address, city, lat, lng, name, phone, province, gym_id) FROM stdin;
935c1c75-3ff4-45ec-b8b5-23484ede4cd8	2026-06-18 18:06:30.837943-03	2026-06-18 18:06:30.838944-03	t	Av. Corrientes 1234	string	-90.0000000	-180.0000000	Sede Central	2613611234	Buenos Aires	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
\.


--
-- TOC entry 4879 (class 0 OID 33474)
-- Dependencies: 218
-- Data for Name: gyms; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.gyms (id, created_at, updated_at, is_active, email, logo_url, name, phone, revenue_share_pct, slug) FROM stdin;
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	2026-06-18 17:53:23.70514-03	2026-06-18 17:53:23.70514-03	t	admin@gimnasio.com	\N	Gimnasio Demo	\N	10.00	gimnasio-demo
\.


--
-- TOC entry 4880 (class 0 OID 33481)
-- Dependencies: 219
-- Data for Name: member_statuses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.member_statuses (id, created_at, updated_at, active, code, description) FROM stdin;
ff00a67d-2a84-4239-8e08-e52c9a96cf6b	2026-06-18 17:53:26.917923-03	2026-06-18 17:53:26.917923-03	t	ACTIVE	Socio activo
48128cf2-7a6a-41dd-9e46-cb2b947fe01d	2026-06-18 17:53:26.917923-03	2026-06-18 17:53:26.917923-03	t	SUSPENDED	Socio sin membresía activa
d2a208b8-8fc7-4de9-bfc2-6730b201bcbc	2026-06-18 17:53:26.917923-03	2026-06-18 17:53:26.917923-03	t	CANCELLED	Socio dado de baja
\.


--
-- TOC entry 4881 (class 0 OID 33486)
-- Dependencies: 220
-- Data for Name: members; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.members (id, created_at, updated_at, birth_date, dni, email, first_name, last_name, notes, phone, branch_id, gym_id, status_id) FROM stdin;
092a5a1a-32cb-433a-b61b-7cc6225d7cc1	2026-06-20 16:20:46.657573-03	2026-06-20 16:20:46.657573-03	2002-06-20	443212442	string@gmail.com	Bruno	Palombarini	string	2613611234	935c1c75-3ff4-45ec-b8b5-23484ede4cd8	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	ff00a67d-2a84-4239-8e08-e52c9a96cf6b
1d1a8178-a847-4c9a-9535-d02069816895	2026-06-24 21:37:16.800542-03	2026-06-24 21:39:00.020144-03	2016-06-25	string	string@gmail.com	carlos	gardel	string	string	935c1c75-3ff4-45ec-b8b5-23484ede4cd8	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	48128cf2-7a6a-41dd-9e46-cb2b947fe01d
b4487a98-c292-423a-9eb4-9abe5fe8d080	2026-06-25 12:28:03.545582-03	2026-06-25 12:28:03.545582-03	\N	28441223	maria@email.com	María	González	\N	1144556677	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	ff00a67d-2a84-4239-8e08-e52c9a96cf6b
\.


--
-- TOC entry 4882 (class 0 OID 33493)
-- Dependencies: 221
-- Data for Name: memberships; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.memberships (id, created_at, updated_at, end_date, frozen_days, frozen_since, notes, price_paid, start_date, status, gym_id, member_id, plan_id) FROM stdin;
fb4eb3de-e089-4417-93a7-61d4423b68e0	2026-06-20 16:24:33.513502-03	2026-06-20 16:24:33.513502-03	2026-07-20	0	\N	string	5000.00	2026-06-20	ACTIVE	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	092a5a1a-32cb-433a-b61b-7cc6225d7cc1	e5e8e7b5-fdc5-44c0-b755-fe73623ec991
dcbfb385-b9a3-4813-889e-89aae690ae17	2026-06-24 21:38:06.519814-03	2026-06-24 21:39:00.015145-03	2022-07-25	0	\N	string	5000.00	2022-06-25	EXPIRED	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	1d1a8178-a847-4c9a-9535-d02069816895	e5e8e7b5-fdc5-44c0-b755-fe73623ec991
b4006af6-a5e5-4519-8ea0-570c38463170	2026-06-25 12:32:25.08198-03	2026-06-25 12:32:25.08198-03	2026-07-25	0	\N	\N	5000.00	2026-06-25	ACTIVE	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	b4487a98-c292-423a-9eb4-9abe5fe8d080	e5e8e7b5-fdc5-44c0-b755-fe73623ec991
\.


--
-- TOC entry 4883 (class 0 OID 33501)
-- Dependencies: 222
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.payments (id, created_at, updated_at, amount, currency, notes, payment_method, reference_code, revenue_share_amount, revenue_share_paid, status, gym_id, member_id, membership_id, registered_by) FROM stdin;
06373596-51a7-49e5-a968-87a3d9f96f13	2026-06-20 16:25:56.609209-03	2026-06-20 16:25:56.609209-03	5000.00	ARS	notas locas	CASH	string	500.00	f	COMPLETED	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	092a5a1a-32cb-433a-b61b-7cc6225d7cc1	fb4eb3de-e089-4417-93a7-61d4423b68e0	cccccccc-cccc-cccc-cccc-cccccccccccc
7e26455d-9d71-417f-836d-9bab426e0df2	2026-06-25 12:34:44.531421-03	2026-06-25 12:34:44.531421-03	5000.00	ARS	\N	CASH	\N	500.00	f	COMPLETED	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	b4487a98-c292-423a-9eb4-9abe5fe8d080	b4006af6-a5e5-4519-8ea0-570c38463170	cccccccc-cccc-cccc-cccc-cccccccccccc
\.


--
-- TOC entry 4884 (class 0 OID 33510)
-- Dependencies: 223
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.permissions (id, created_at, updated_at, code, description, module) FROM stdin;
c445fb26-3deb-4707-8192-76808b2780f6	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBER_VIEW	Ver socios	MEMBERS
500d38fd-661c-4133-8786-1ad610d978a2	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBER_CREATE	Crear socios	MEMBERS
08155020-5adc-4b1a-a9e5-7cbd1c028930	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBER_EDIT	Editar socios	MEMBERS
0dbe48bf-36c9-472f-b787-678b07898f3e	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBER_CANCEL	Cancelar socios	MEMBERS
aa307d4b-c462-4fd6-9ea4-a52599462118	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PLAN_VIEW	Ver planes	PLANS
f4027228-e771-4ecb-86b1-8dacde3cb175	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PLAN_CREATE	Crear planes	PLANS
122253ee-c0e7-4315-b564-00d938137ee0	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PLAN_EDIT	Editar planes	PLANS
981adb71-0fed-43e3-b3ce-4f3b18d5a382	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PLAN_DEACTIVATE	Desactivar planes	PLANS
41f9c6d2-d42f-4fbf-b285-0dbc4f785081	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBERSHIP_VIEW	Ver membresías	MEMBERSHIPS
8c052393-e1b4-44d1-a4c6-2779a0366b73	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBERSHIP_CREATE	Crear membresías	MEMBERSHIPS
86c25640-665b-4932-9b26-97a1e2ede3b4	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBERSHIP_FREEZE	Congelar membresías	MEMBERSHIPS
e3757184-e47c-407b-bbcb-5f3693df6101	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	MEMBERSHIP_CANCEL	Cancelar membresías	MEMBERSHIPS
cff64682-0bee-4361-a103-7bf5052c7a95	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PAYMENT_VIEW	Ver pagos	PAYMENTS
0cc66cd7-6c66-44cf-b8bc-25063bc88c1b	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PAYMENT_CREATE	Registrar pagos	PAYMENTS
ace1e9cd-0aeb-49e2-85f8-01732f10e440	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	PAYMENT_REFUND	Reembolsar pagos	PAYMENTS
443cfdbc-81f4-4c80-a49a-b4e324422748	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	BRANCH_VIEW	Ver sucursales	BRANCHES
3e1072fc-6a99-4ce8-a914-8f61476ad9a2	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	BRANCH_CREATE	Crear sucursales	BRANCHES
3c236b9e-241b-40a1-a7ea-3070b4c288d2	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	BRANCH_EDIT	Editar sucursales	BRANCHES
939bbe78-1fd3-4796-8b2c-1699f1552a10	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	BRANCH_DEACTIVATE	Desactivar sucursales	BRANCHES
f662b6d5-bc28-46ba-9a2b-392abdac47bd	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	USER_VIEW	Ver usuarios	USERS
0f3ba7ec-3533-495a-9841-62dd9353f0c4	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	USER_CREATE	Crear usuarios	USERS
8b38857d-39ef-4f98-aad6-531971b10a71	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	USER_EDIT	Editar usuarios	USERS
f650ec19-92a9-4b4a-994c-b87b2730d8a9	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	ROLE_VIEW	Ver roles	ROLES
a317350a-b100-4332-8ae1-b616a2acf702	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	ROLE_CREATE	Crear roles	ROLES
e8e11aab-4d4c-4564-ba32-13989459f9ad	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	ROLE_EDIT	Editar roles	ROLES
72f87929-9c7f-4b95-81d3-ff3ac5b1ae57	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	DASHBOARD_VIEW	Ver dashboard	DASHBOARD
aecdb2e7-c4cf-4daf-a6df-9e38651f5a8a	2026-06-18 17:53:48.603911-03	2026-06-18 17:53:48.603911-03	REPORT_VIEW	Ver reportes	REPORTS
\.


--
-- TOC entry 4885 (class 0 OID 33515)
-- Dependencies: 224
-- Data for Name: plans; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.plans (id, created_at, updated_at, is_active, currency, description, duration_days, name, price, is_public, sessions_per_week, gym_id) FROM stdin;
e5e8e7b5-fdc5-44c0-b755-fe73623ec991	2026-06-20 16:22:28.815827-03	2026-06-20 16:22:28.815827-03	t	ARS	un plan q dura un mes q mas 	30	Plan Mensual	5000.00	t	2	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
\.


--
-- TOC entry 4886 (class 0 OID 33522)
-- Dependencies: 225
-- Data for Name: role_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role_permissions (role_id, permission_id) FROM stdin;
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	c445fb26-3deb-4707-8192-76808b2780f6
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	500d38fd-661c-4133-8786-1ad610d978a2
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	08155020-5adc-4b1a-a9e5-7cbd1c028930
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	0dbe48bf-36c9-472f-b787-678b07898f3e
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	aa307d4b-c462-4fd6-9ea4-a52599462118
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	f4027228-e771-4ecb-86b1-8dacde3cb175
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	122253ee-c0e7-4315-b564-00d938137ee0
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	981adb71-0fed-43e3-b3ce-4f3b18d5a382
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	41f9c6d2-d42f-4fbf-b285-0dbc4f785081
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	8c052393-e1b4-44d1-a4c6-2779a0366b73
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	86c25640-665b-4932-9b26-97a1e2ede3b4
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	e3757184-e47c-407b-bbcb-5f3693df6101
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	cff64682-0bee-4361-a103-7bf5052c7a95
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	0cc66cd7-6c66-44cf-b8bc-25063bc88c1b
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	ace1e9cd-0aeb-49e2-85f8-01732f10e440
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	443cfdbc-81f4-4c80-a49a-b4e324422748
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	3e1072fc-6a99-4ce8-a914-8f61476ad9a2
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	3c236b9e-241b-40a1-a7ea-3070b4c288d2
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	939bbe78-1fd3-4796-8b2c-1699f1552a10
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	f662b6d5-bc28-46ba-9a2b-392abdac47bd
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	0f3ba7ec-3533-495a-9841-62dd9353f0c4
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	8b38857d-39ef-4f98-aad6-531971b10a71
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	f650ec19-92a9-4b4a-994c-b87b2730d8a9
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	a317350a-b100-4332-8ae1-b616a2acf702
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	e8e11aab-4d4c-4564-ba32-13989459f9ad
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	72f87929-9c7f-4b95-81d3-ff3ac5b1ae57
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	aecdb2e7-c4cf-4daf-a6df-9e38651f5a8a
\.


--
-- TOC entry 4887 (class 0 OID 33527)
-- Dependencies: 226
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (id, created_at, updated_at, active, description, name, gym_id) FROM stdin;
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb	2026-06-18 17:53:51.844607-03	2026-06-18 17:53:51.844607-03	t	Acceso total	Owner	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
\.


--
-- TOC entry 4888 (class 0 OID 33532)
-- Dependencies: 227
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, created_at, updated_at, is_active, email, first_name, last_login_at, last_name, password_hash, branch_id, gym_id, role_id) FROM stdin;
cccccccc-cccc-cccc-cccc-cccccccccccc	2026-06-18 17:54:00.000471-03	2026-06-18 17:54:00.000471-03	t	admin@gimnasio.com	Admin	\N	Demo	$2a$12$5x1QQT2ivZxrE9RUEWJW0OKsl2E3ly2msfq89A6LWllOCUWLlWbP.	\N	aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa	bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
\.


--
-- TOC entry 4684 (class 2606 OID 33473)
-- Name: branches branches_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.branches
    ADD CONSTRAINT branches_pkey PRIMARY KEY (id);


--
-- TOC entry 4686 (class 2606 OID 33480)
-- Name: gyms gyms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gyms
    ADD CONSTRAINT gyms_pkey PRIMARY KEY (id);


--
-- TOC entry 4690 (class 2606 OID 33485)
-- Name: member_statuses member_statuses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.member_statuses
    ADD CONSTRAINT member_statuses_pkey PRIMARY KEY (id);


--
-- TOC entry 4694 (class 2606 OID 33492)
-- Name: members members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);


--
-- TOC entry 4698 (class 2606 OID 33500)
-- Name: memberships memberships_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.memberships
    ADD CONSTRAINT memberships_pkey PRIMARY KEY (id);


--
-- TOC entry 4700 (class 2606 OID 33509)
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- TOC entry 4702 (class 2606 OID 33514)
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- TOC entry 4706 (class 2606 OID 33521)
-- Name: plans plans_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plans
    ADD CONSTRAINT plans_pkey PRIMARY KEY (id);


--
-- TOC entry 4708 (class 2606 OID 33526)
-- Name: role_permissions role_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id);


--
-- TOC entry 4710 (class 2606 OID 33531)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 4696 (class 2606 OID 33544)
-- Name: members uk41e81awg32ttuvu0trg5qr05y; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT uk41e81awg32ttuvu0trg5qr05y UNIQUE (gym_id, dni);


--
-- TOC entry 4712 (class 2606 OID 33548)
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- TOC entry 4704 (class 2606 OID 33546)
-- Name: permissions uk7lcb6glmvwlro3p2w2cewxtvd; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT uk7lcb6glmvwlro3p2w2cewxtvd UNIQUE (code);


--
-- TOC entry 4692 (class 2606 OID 33542)
-- Name: member_statuses uk8kkt37fi9oc50gqkdb9pv88su; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.member_statuses
    ADD CONSTRAINT uk8kkt37fi9oc50gqkdb9pv88su UNIQUE (code);


--
-- TOC entry 4688 (class 2606 OID 33540)
-- Name: gyms uks4940hiwfptpvbqdyhscqa4dt; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gyms
    ADD CONSTRAINT uks4940hiwfptpvbqdyhscqa4dt UNIQUE (slug);


--
-- TOC entry 4714 (class 2606 OID 33538)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4726 (class 2606 OID 33604)
-- Name: plans fk1jhwb9nnyutjm0rwh04dmcb7n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plans
    ADD CONSTRAINT fk1jhwb9nnyutjm0rwh04dmcb7n FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4719 (class 2606 OID 33569)
-- Name: memberships fk3ppeyyqqvlw7h2ic2pgl3c14o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.memberships
    ADD CONSTRAINT fk3ppeyyqqvlw7h2ic2pgl3c14o FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4730 (class 2606 OID 33629)
-- Name: users fk61re4b3t50tt71ru6l5mld7c2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk61re4b3t50tt71ru6l5mld7c2 FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4731 (class 2606 OID 33624)
-- Name: users fk9o70sp9ku40077y38fk4wieyk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk9o70sp9ku40077y38fk4wieyk FOREIGN KEY (branch_id) REFERENCES public.branches(id);


--
-- TOC entry 4722 (class 2606 OID 33599)
-- Name: payments fka8auke8s013pwjnrc9bk8okcm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fka8auke8s013pwjnrc9bk8okcm FOREIGN KEY (registered_by) REFERENCES public.users(id);


--
-- TOC entry 4715 (class 2606 OID 33549)
-- Name: branches fkcs2bybmlkqmxltpquxg9tsiir; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.branches
    ADD CONSTRAINT fkcs2bybmlkqmxltpquxg9tsiir FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4723 (class 2606 OID 33594)
-- Name: payments fkdk5hyohn960o3k8mb4m1ptyv6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fkdk5hyohn960o3k8mb4m1ptyv6 FOREIGN KEY (membership_id) REFERENCES public.memberships(id);


--
-- TOC entry 4727 (class 2606 OID 33609)
-- Name: role_permissions fkegdk29eiy7mdtefy5c7eirr6e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT fkegdk29eiy7mdtefy5c7eirr6e FOREIGN KEY (permission_id) REFERENCES public.permissions(id);


--
-- TOC entry 4720 (class 2606 OID 33579)
-- Name: memberships fkesht7qgvoekbkt3s0imo1grqq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.memberships
    ADD CONSTRAINT fkesht7qgvoekbkt3s0imo1grqq FOREIGN KEY (plan_id) REFERENCES public.plans(id);


--
-- TOC entry 4721 (class 2606 OID 33574)
-- Name: memberships fkkv2gm8d7nl8u1nrfvxt6tbwhc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.memberships
    ADD CONSTRAINT fkkv2gm8d7nl8u1nrfvxt6tbwhc FOREIGN KEY (member_id) REFERENCES public.members(id);


--
-- TOC entry 4729 (class 2606 OID 33619)
-- Name: roles fkl130p6xf5vyk3xl61cw7j2twb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT fkl130p6xf5vyk3xl61cw7j2twb FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4716 (class 2606 OID 33554)
-- Name: members fkmip10cfr063gjykgmnf1uy5u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT fkmip10cfr063gjykgmnf1uy5u FOREIGN KEY (branch_id) REFERENCES public.branches(id);


--
-- TOC entry 4728 (class 2606 OID 33614)
-- Name: role_permissions fkn5fotdgk8d1xvo8nav9uv3muc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT fkn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 4732 (class 2606 OID 33634)
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 4717 (class 2606 OID 33559)
-- Name: members fkq6ilkrakw75dgu4bfknklqdor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT fkq6ilkrakw75dgu4bfknklqdor FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4718 (class 2606 OID 33564)
-- Name: members fkr3cs8cnbimhoolcecuppdraec; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT fkr3cs8cnbimhoolcecuppdraec FOREIGN KEY (status_id) REFERENCES public.member_statuses(id);


--
-- TOC entry 4724 (class 2606 OID 33584)
-- Name: payments fks5h2o9wbsjtyqvebmsuxfq4y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fks5h2o9wbsjtyqvebmsuxfq4y FOREIGN KEY (gym_id) REFERENCES public.gyms(id);


--
-- TOC entry 4725 (class 2606 OID 33589)
-- Name: payments fktvbq19graff4nnoqpngbe762; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fktvbq19graff4nnoqpngbe762 FOREIGN KEY (member_id) REFERENCES public.members(id);


-- Rehabilitar restricciones FK
SET session_replication_role = DEFAULT;

-- Completed on 2026-06-25 17:38:49

--
-- PostgreSQL database dump complete
--

