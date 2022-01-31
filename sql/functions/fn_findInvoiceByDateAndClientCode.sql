-- FUNCTION: public.fn_findInvoiceByDateAndClientCode(date, date, text)

-- DROP FUNCTION public."fn_findInvoiceByDateAndClientCode"(date, date, text);

CREATE OR REPLACE FUNCTION public."fn_findInvoiceByDateAndClientCode"(
	start_date date,
	end_date date,
	client_code text)
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
INNER JOIN individual c
ON i.client_id = c.id
WHERE (i.status NOT IN (2))
AND (t.date BETWEEN start_date AND end_date)
AND (UPPER(c.code) LIKE UPPER(client_code))
ORDER BY(t.date);
$BODY$;

ALTER FUNCTION public."fn_findInvoiceByDateAndClientCode"(date, date, text)
    OWNER TO "reslide-db-user";
