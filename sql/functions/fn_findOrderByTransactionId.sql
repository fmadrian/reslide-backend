-- FUNCTION: public.fn_findOrderByTransactionId(bigint)

-- DROP FUNCTION public."fn_findOrderByTransactionId"(bigint);

CREATE OR REPLACE FUNCTION public."fn_findOrderByTransactionId"(
	p_transaction_id bigint)
    RETURNS order_reslide
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
-- Had to rename the parameter to 'p_transaction_id' because it clashed with
-- the name in the table.
SELECT o.* 
FROM order_reslide o
WHERE o.transaction_id = p_transaction_id;
$BODY$;

ALTER FUNCTION public."fn_findOrderByTransactionId"(bigint)
    OWNER TO "reslide-db-user";
