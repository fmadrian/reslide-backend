-- FUNCTION: public.fn_findOrderByDateActualDateProviderCode(date, date, date, date, text)

-- DROP FUNCTION public."fn_findOrderByDateActualDateProviderCode"(date, date, date, date, text);

CREATE OR REPLACE FUNCTION public."fn_findOrderByDateActualDateProviderCode"(
	start_date date,
	end_date date,
	start_actual_delivery_date date,
	end_actual_delivery_date date,
	provider_code text)
    RETURNS TABLE("like" order_reslide) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	IF provider_code != '' THEN
		RETURN QUERY (SELECT o.* 
		FROM order_reslide o
		INNER JOIN transaction t
		ON o.transaction_id = t.id
		INNER JOIN individual c
		ON o.provider_id = c.id
		WHERE (o.status IN (0))
		AND (((t.date BETWEEN start_date AND end_date) 
			  AND o.actual_delivery_date BETWEEN start_actual_delivery_date AND end_actual_delivery_date)
		AND (UPPER(c.code) LIKE UPPER(provider_code)))
		ORDER BY(t.date));
	ELSE
		RETURN QUERY (SELECT o.* 
		FROM order_reslide o
		INNER JOIN transaction t
		ON o.transaction_id = t.id
		INNER JOIN individual c
		ON o.provider_id = c.id
		WHERE (o.status IN (0))
		AND ((t.date BETWEEN start_date AND end_date) 
			  AND o.actual_delivery_date BETWEEN start_actual_delivery_date AND end_actual_delivery_date)
		ORDER BY(t.date));
	END IF;
END;
$BODY$;

ALTER FUNCTION public."fn_findOrderByDateActualDateProviderCode"(date, date, date, date, text)
    OWNER TO postgres;
