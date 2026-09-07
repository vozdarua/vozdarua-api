CREATE TABLE public.feedback (
    createdat timestamp(6) with time zone,
    id bigint NOT NULL,
    updatedat timestamp(6) with time zone,
    email character varying(255),
    message character varying(1000),
    name character varying(255),
    type character varying(255)
);
CREATE SEQUENCE public.feedback_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT feedback_pkey PRIMARY KEY (id);
