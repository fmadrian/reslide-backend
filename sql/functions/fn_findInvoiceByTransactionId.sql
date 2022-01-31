-- FUNCTION: public.fn_findInvoiceByTransactionId(bigint)

-- DROP FUNCTION public."fn_findInvoiceByTransactionId"(bigint);

CREATE OR REPLACE FUNCTION public."fn_findInvoiceByTransactionId"(
	p_transaction_id bigint)
    RETURNS invoice
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
-- Had to rename the parameter to 'p_transaction_id' because it clashed with
-- the name in the table.
SELECT i.* 
FROM invoice i
WHERE i.transaction_id = p_transaction_id;
$BODY$;

ALTER FUNCTION public."fn_findInvoiceByTransactionId"(bigint)
    OWNER TO "reslide-db-user";
