-- FUNCTION: public.fn_findOrderByPaymentId(bigint)

-- DROP FUNCTION public."fn_findOrderByPaymentId"(bigint);

CREATE OR REPLACE FUNCTION public."fn_findOrderByPaymentId"(
	payment_id bigint)
    RETURNS TABLE("like" order_reslide) 
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
SELECT o.*
FROM order_reslide o
INNER JOIN transaction t
ON o.transaction_id = t.id
INNER JOIN payment p
ON p.transaction_id = t.id
WHERE p.id = payment_id;
$BODY$;

ALTER FUNCTION public."fn_findOrderByPaymentId"(bigint)
    OWNER TO "reslide-db-user";
