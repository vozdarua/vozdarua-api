ALTER TABLE public.address ADD COLUMN city_id bigint REFERENCES public.city(id);
ALTER TABLE public.address ADD COLUMN state_id bigint REFERENCES public.state(id);
CREATE INDEX idx_address_city_id ON public.address (city_id);
