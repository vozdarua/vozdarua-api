-- Schema inicial, extraído do que o Hibernate gera hoje a partir das entidades
-- (pg_dump --schema-only em cima do banco dev). Daqui pra frente, mudança de schema
-- só entra via nova migration — o Hibernate roda em modo "validate".

CREATE TABLE public.address (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    cep character varying(255),
    city character varying(255),
    neighborhood character varying(255),
    number character varying(255),
    state character varying(255),
    street character varying(255)
);
CREATE SEQUENCE public.address_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.category (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    description character varying(500),
    icon character varying(255),
    name character varying(255) NOT NULL
);
CREATE SEQUENCE public.category_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.category_tags (
    category_id bigint NOT NULL,
    tag character varying(255)
);

CREATE TABLE public.comment (
    author_id bigint,
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    issue_id bigint NOT NULL,
    useragent character varying(500),
    text character varying(1000) NOT NULL,
    authoremail character varying(255),
    ipaddress character varying(255)
);
CREATE SEQUENCE public.comment_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.image (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    name character varying(255),
    s3key character varying(255),
    s3url character varying(255)
);
CREATE SEQUENCE public.image_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.issue (
    anonymous boolean NOT NULL,
    confirmissue integer,
    address_id bigint,
    category_id bigint,
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    photo_id bigint,
    reporter_id bigint,
    severity_id bigint,
    status_id bigint,
    updatedat timestamp(6) with time zone,
    description character varying(1000)
);
CREATE SEQUENCE public.issue_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.passwordresettoken (
    expirydate timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    token character varying(255) NOT NULL
);
CREATE SEQUENCE public.passwordresettoken_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.severity (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    icon character varying(255),
    name character varying(255) NOT NULL
);
CREATE SEQUENCE public.severity_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.state (
    uf character varying(2),
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    name character varying(255)
);
CREATE SEQUENCE public.state_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.status (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    icon character varying(255),
    name character varying(255) NOT NULL
);
CREATE SEQUENCE public.status_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.vozdaruauser (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    lastresetrequest timestamp(6) without time zone,
    updatedat timestamp(6) with time zone,
    email character varying(255),
    password character varying(255),
    phone character varying(255),
    role character varying(255)
);
CREATE SEQUENCE public.vozdaruauser_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_name_key UNIQUE (name);
ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.image
    ADD CONSTRAINT image_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_photo_id_key UNIQUE (photo_id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.passwordresettoken
    ADD CONSTRAINT passwordresettoken_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.passwordresettoken
    ADD CONSTRAINT passwordresettoken_token_key UNIQUE (token);
ALTER TABLE ONLY public.passwordresettoken
    ADD CONSTRAINT passwordresettoken_user_id_key UNIQUE (user_id);
ALTER TABLE ONLY public.severity
    ADD CONSTRAINT severity_name_key UNIQUE (name);
ALTER TABLE ONLY public.severity
    ADD CONSTRAINT severity_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.state
    ADD CONSTRAINT state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_name_key UNIQUE (name);
ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.vozdaruauser
    ADD CONSTRAINT vozdaruauser_email_key UNIQUE (email);
ALTER TABLE ONLY public.vozdaruauser
    ADD CONSTRAINT vozdaruauser_phone_key UNIQUE (phone);
ALTER TABLE ONLY public.vozdaruauser
    ADD CONSTRAINT vozdaruauser_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.comment
    ADD CONSTRAINT fk3vet4nnineyrhag64sou5qymi FOREIGN KEY (author_id) REFERENCES public.vozdaruauser(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fk7jiwrq1aq1g9h1rrmmkpbppg FOREIGN KEY (reporter_id) REFERENCES public.vozdaruauser(id);
ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT fkd5pjw10u6ypo1oqwvh9kw85x6 FOREIGN KEY (category_id) REFERENCES public.category(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fkdcrrw3qh382ar7hj5fs8bubk8 FOREIGN KEY (status_id) REFERENCES public.status(id);
ALTER TABLE ONLY public.comment
    ADD CONSTRAINT fker01qks6mej0crtej5pbrt6du FOREIGN KEY (issue_id) REFERENCES public.issue(id);
ALTER TABLE ONLY public.passwordresettoken
    ADD CONSTRAINT fkik3j5mhqjptqd6oy0v76boglk FOREIGN KEY (user_id) REFERENCES public.vozdaruauser(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fkiqf0x0ngxj1v1io6o4qro2mpo FOREIGN KEY (address_id) REFERENCES public.address(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fkjnl5c4ms9ix5e0j2k3sypat8q FOREIGN KEY (category_id) REFERENCES public.category(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fkl0butm911bkmem2uaj2b7w9j4 FOREIGN KEY (severity_id) REFERENCES public.severity(id);
ALTER TABLE ONLY public.issue
    ADD CONSTRAINT fks8bp6h44wi5jgt0jy2d3llyno FOREIGN KEY (photo_id) REFERENCES public.image(id);
