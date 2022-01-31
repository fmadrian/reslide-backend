-- FUNCTION: public.fn_findInvoiceByDate(text, text)

-- DROP FUNCTION public."fn_findInvoiceByDate"(text, text);

CREATE OR REPLACE FUNCTION public."fn_findInvoiceByDate"(
	start_date text,
	end_date text)
    RETURNS TABLE("like" invoice) 
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
SELECT i.* 
FROM invoice i
INNER JOIN transaction t
ON i.transaction_id = t.id
WHERE (i.status NOT IN (2))
AND (t.date BETWEEN TO_TIMESTAMP (start_date,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP (end_date,'YYYY-MM-DD HH24:MI:SS'))
ORDER BY(t.date);
$BODY$;

ALTER FUNCTION public."fn_findInvoiceByDate"(text, text)
    OWNER TO "reslide-db-user";
