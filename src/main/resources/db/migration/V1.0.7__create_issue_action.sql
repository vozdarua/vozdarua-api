CREATE TABLE public.issueaction (
    id bigint NOT NULL,
    issue_id bigint NOT NULL REFERENCES public.issue(id) ON DELETE CASCADE,
    identity character varying(255) NOT NULL,
    action character varying(20) NOT NULL,
    createdat timestamp(6) with time zone,
    PRIMARY KEY (id),
    CONSTRAINT uq_issueaction_issue_identity_action UNIQUE (issue_id, identity, action)
);
CREATE SEQUENCE public.issueaction_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
CREATE INDEX idx_issueaction_issue_id ON public.issueaction (issue_id);
