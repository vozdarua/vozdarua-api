ALTER TABLE public.issue ADD COLUMN confirmresolve integer;
UPDATE public.issue SET confirmresolve = 0 WHERE confirmresolve IS NULL;
