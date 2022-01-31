-- FUNCTION: public.fn_findInvoiceByPaymentId(bigint)

-- DROP FUNCTION public."fn_findInvoiceByPaymentId"(bigint);

CREATE OR REPLACE FUNCTION public."fn_findInvoiceByPaymentId"(
	payment_id bigint)
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
INNER JOIN payment p
ON p.transaction_id = t.id
WHERE p.id = payment_id;
$BODY$;

ALTER FUNCTION public."fn_findInvoiceByPaymentId"(bigint)
    OWNER TO "reslide-db-user";
