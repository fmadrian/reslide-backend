-- FUNCTION: public.fn_findOrderByDate(date, date)

-- DROP FUNCTION public."fn_findOrderByDate"(date, date);

CREATE OR REPLACE FUNCTION public."fn_findOrderByDate"(
	start_date date,
	end_date date)
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
WHERE o.status IN (0)
AND ((t.date BETWEEN start_date AND end_date) 
OR (o.expected_delivery_date BETWEEN start_date AND end_date));
$BODY$;

ALTER FUNCTION public."fn_findOrderByDate"(date, date)
    OWNER TO "reslide-db-user";
