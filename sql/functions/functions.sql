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
    OWNER TO "<dbuser>";
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
    OWNER TO "<dbuser>";
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
    OWNER TO "<dbuser>";
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
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByDate(date, date)

-- DROP FUNCTION public.""(date, date);

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
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByDateActualDateProviderCode(date, date, date, date, text)

-- DROP FUNCTION public.""(date, date, date, date, text);

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
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByDateAndProviderCode(date, date, text)

-- DROP FUNCTION public.""(date, date, text);

CREATE OR REPLACE FUNCTION public."fn_findOrderByDateAndProviderCode"(
	start_date date,
	end_date date,
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
	AND (t.date BETWEEN start_date AND end_date)
	AND (UPPER(c.code) LIKE UPPER(provider_code))
	ORDER BY(t.date));
ELSE
	RETURN QUERY (SELECT o.* 
	FROM order_reslide o
	INNER JOIN transaction t
	ON o.transaction_id = t.id
	INNER JOIN individual c
	ON o.provider_id = c.id
	WHERE (o.status IN (0))
	AND (t.date BETWEEN start_date AND end_date)
	ORDER BY(t.date));
END IF;
END
$BODY$;

ALTER FUNCTION public."fn_findOrderByDateAndProviderCode"(date, date, text)
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByDateExpectedDateActualDateProviderCode(date, date, date, date, date, date, text)

-- DROP FUNCTION public.""(date, date, date, date, date, date, text);

CREATE OR REPLACE FUNCTION public."fn_findOrderByDateExpectedDateActualDateProviderCode"(
	start_date date,
	end_date date,
	start_expected_delivery_date date,
	end_expected_delivery_date date,
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
-- If doesn't include a provider code, it uses the query where it is not taken into account.
	IF provider_code != '' THEN
		RETURN QUERY (SELECT o.* 
		FROM order_reslide o
		INNER JOIN transaction t
		ON o.transaction_id = t.id
		INNER JOIN individual c
		ON o.provider_id = c.id
		WHERE (o.status IN (0))
		AND (((t.date BETWEEN start_date AND end_date) 
			  AND o.expected_delivery_date BETWEEN start_expected_delivery_date AND end_expected_delivery_date)
			 AND o.actual_delivery_date BETWEEN start_actual_delivery_date AND end_actual_delivery_date)
		AND (UPPER(c.code) LIKE UPPER(provider_code))
		ORDER BY(t.date));
	ELSE
		RETURN QUERY (SELECT o.* 
		FROM order_reslide o
		INNER JOIN transaction t
		ON o.transaction_id = t.id
		INNER JOIN individual c
		ON o.provider_id = c.id
		WHERE (o.status IN (0))
		AND (((t.date BETWEEN start_date AND end_date) 
			  AND o.expected_delivery_date BETWEEN start_expected_delivery_date AND end_expected_delivery_date)
			 AND o.actual_delivery_date BETWEEN start_actual_delivery_date AND end_actual_delivery_date)
		ORDER BY(t.date));
	END IF;
END;
$BODY$;

ALTER FUNCTION public."fn_findOrderByDateExpectedDateActualDateProviderCode"(date, date, date, date, date, date, text)
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByDateExpectedDateProviderCode(date, date, date, date, text)

-- DROP FUNCTION public."fn_findOrderByDateExpectedDateProviderCode"(date, date, date, date, text);

CREATE OR REPLACE FUNCTION public."fn_findOrderByDateExpectedDateProviderCode"(
	start_date date,
	end_date date,
	start_expected_delivery_date date,
	end_expected_delivery_date date,
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
			  AND o.expected_delivery_date BETWEEN start_expected_delivery_date AND end_expected_delivery_date)
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
			  AND o.expected_delivery_date BETWEEN start_expected_delivery_date AND end_expected_delivery_date)
		ORDER BY(t.date));
	END IF;
END;
$BODY$;

ALTER FUNCTION public."fn_findOrderByDateExpectedDateProviderCode"(date, date, date, date, text)
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByPaymentId(bigint)

-- DROP FUNCTION public.""(bigint);

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
    OWNER TO "<dbuser>";
-- FUNCTION: public.fn_findOrderByTransactionId(bigint)

-- DROP FUNCTION public.""(bigint);

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
    OWNER TO "<dbuser>";