CREATE TABLE public.city (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    state_id bigint NOT NULL REFERENCES public.state(id),
    createdat timestamp(6) with time zone,
    updatedat timestamp(6) with time zone,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_city_name_state ON public.city (LOWER(name), state_id);
CREATE INDEX idx_city_state_id ON public.city (state_id);

CREATE SEQUENCE public.city_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
