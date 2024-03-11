/*CREATE OR REPLACE VIEW `ordersRowsView`
AS
SELECT
    rig_ordc.NUME_PRO AS NUME_PRO,
    rig_ordc.NUME AS NUME,
    rig_ordc.STAMP AS STAM,
    rig_ordc.RIGA AS RIGA,
    rig_ordc.CORTO AS CORTO,
    TRIM(rig_ordc.ART_CODI) AS ART_CODI,
    TRIM(rig_ordc.ART_CFOR) AS ART_CFOR,
    TRIM(rig_ordc.DESCRI) AS DESCRI,
    TRIM(rig_ordc.LIBE) AS LIBE,
    TRIM(rig_ordc.IVA) AS IVA,
    rig_ordc.IVA_PERC AS IVA_PERC,
    rig_ordc.COST AS COST,
    rig_ordc.VEND AS VEND,
    TRIM(lis_ordr.MISU) AS MISU,
    lis_ordr.QTA AS QTA,
    lis_ordr.QTA_CONS AS QTA_CONS,
    lis_ordr.QTA_CONS AS QTA_CONS,
    lis_ordr.QTA_CONS AS*/










####################################################################################################
CREATE OR REPLACE VIEW `ordersview`
AS
SELECT
    lis_ordc.NUME AS NUME,
    lis_ordc.CODI AS CODI,
    IFNULL(TRIM(clienti.DESCRI),'') AS RAG_SOC,
    IFNULL(IFNULL(TRIM(destina.VIA), TRIM(clienti.VIA)), '') AS VIA,
    IFNULL(IFNULL(TRIM(destina.CAP), TRIM(clienti.CAP)), '') AS CAP,
    IFNULL(IFNULL(TRIM(destina.CITTA), TRIM(clienti.CITTA)), '') AS CITTA,
    IFNULL(IFNULL(TRIM(destina.PROV), TRIM(clienti.PROV)), '') AS PROV,
    IFNULL(TRIM(clienti.NAZIONE), '') AS NAZIONE,
    IFNULL(TRIM(lis_ordc.DESCRI), '') AS DESCRI,
    IFNULL(TRIM(destina.DESCRI), '') AS DESTINA_DESCRI,
    lis_ordc.N_LAV AS N_LAV,
    lis_ordc.DATAI AS DATAI,
    lis_ordc.AGENTE AS AGENTE,
    lis_ordc.DESTINA AS DESTINA,
    TRIM(lis_ordc.TRASPO) AS TRASPO,
    lis_ordc.COLLI AS COLLI,
    IFNULL(TRIM(lis_ordc.VETTORE), TRIM(vettori.DESCRI)) AS VETTORE,
    lis_ordc.VETT_NUME AS VETT_NUME,
    lis_ordc.PESO AS PESO,
    TRIM(lis_ordc.NUME_ORDI) AS NUME_ORDI,
    lis_ordc.DATA_ORDI AS DATA_ORDI,
    TRIM(lis_ordc.NOTE) AS NOTE,
    lis_ordc.D_CONSEGNA AS D_CONSEGNA,
    lis_ordc.TASSATIVA AS TASSATIVA,
    lis_ordc.CONSEGNA AS CONSEGNA,
    lis_ordc.STATO_CONS AS STATO_CONS,
    lis_ordc.URGENTE AS URGENTE,
    lis_ordc.PARZIALE AS PARZIALE,
    lis_ordc.NUMERO AS NUMERO

FROM lis_ordc

LEFT JOIN clienti ON lis_ordc.CODI = clienti.CODI
LEFT JOIN destina ON lis_ordc.DESTINA = destina.PROG_TUTTO
LEFT JOIN vettori ON lis_ordc.VETT_NUME = vettori.NUME
WHERE lis_ordc.DATAI >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
      AND !lis_ordc.CONSOLI

ORDER BY lis_ordc.DATAI DESC;

####################################################################################################
CREATE OR REPLACE VIEW `clientsview`
AS
SELECT
    clienti.CODI AS CODI,
    TRIM(clienti.DESCRI) AS DESCRI,
    TRIM(clienti.VIA) AS VIA,
    TRIM(clienti.CAP) AS CAP,
    TRIM(clienti.CITTA) AS CITTA,
    TRIM(clienti.PROV) AS PROV,
    TRIM(clienti.NAZIONE) AS NAZIONE,
    TRIM(clienti.DESC2) AS DESC2,
    TRIM(clienti.PIVA) AS PIVA,
    TRIM(clienti.COD_FIS) AS CODFIS
FROM clienti
WHERE !clienti.BLOC AND clienti.DESCRI <> ''
ORDER BY clienti.DESCRI;

####################################################################################################
#Function to calculate the last digit of an EAN13 code (12 digits + 1 check digit)
# and EAN8 code (7 digits + 1 check digit)

DROP FUNCTION IF EXISTS EanLastDigit;
CREATE FUNCTION EanLastDigit(partEan VARCHAR(12)) RETURNS CHAR CHARSET latin1
READS SQL DATA
BEGIN

    DECLARE x INT DEFAULT 0;
    DECLARE a INT DEFAULT 0;
    DECLARE b INT DEFAULT 0;
    DECLARE lastDigit CHAR(1) DEFAULT '';

    IF NOT(LENGTH(partEan) = 12 OR LENGTH(partEan) = 7) THEN
        RETURN lastDigit;
    END IF;

    SET x = 2;
    WHILE x <= LENGTH(partEan) - 2 DO
        SET x = x + 2;
        SET a = a + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
    END WHILE;

    SET a = a * 3;

    SET x = 1;

    WHILE x <=LENGTH(partEan) - 3 DO
        SET x = x + 2;
        SET b = b + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
    END WHILE;

    SET a = a + b;
    SET b = CAST((a / 10) AS SIGNED);
    SET b = (b * 10) + 10;
    SET a = b - a;

    IF a <> 10 THEN
        SET lastDigit = CAST(LEFT(a,1) AS CHAR);
    ELSE
        SET lastDigit = '0';
    END IF;

    RETURN lastDigit;
END;


####################################################################################################
CREATE OR REPLACE VIEW `articlesview`
AS
SELECT
     arti.CORTO AS CORTO,
     TRIM(arti.CODI) AS CODI,
     TRIM(arti.CFOR) AS CFOR,
     TRIM(arti.DESCRI) AS DESCRI,
     TRIM(arti.IVA) AS IVA,
     arti.COST AS COST,
     arti.VEND AS VEND,
     TRIM(arti.TIPO) AS TIPO,
     TRIM(arti.TIPO_SOTTO) AS TIPO_SOTTO,
     TRIM(arti.REPA) AS REPA,
     TRIM(arti.REPA_SOTTO) AS REPA_SOTTO,
     TRIM(arti.GRUPPO) AS GRUPPO,
     TRIM(arti.MISU) AS MISU,
    CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'),  EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'))))  AS EAN_8,
    CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0'), EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0')))) AS EAN_13,
    IFNULL(GROUP_CONCAT(TRIM(art_alt.COD_ALT), '|'), '') AS EAN_ALT
FROM arti
LEFT JOIN (
    SELECT
        CORTO,
        COD_ALT,
        ROW_NUMBER() OVER (PARTITION BY CORTO ORDER BY N_PRI) AS rn
    FROM
        art_alt
) AS art_alt ON arti.CORTO = art_alt.CORTO
CROSS JOIN (
    SELECT TRIM(VARIA) AS VARIA FROM costanti WHERE NUME = 20
) AS costanti
GROUP BY
    arti.CORTO,
    arti.CODI,
    arti.CFOR,
    arti.DESCRI,
    arti.IVA,
    arti.COST,
    arti.VEND,
    arti.TIPO,
    arti.TIPO_SOTTO,
    arti.REPA,
    arti.REPA_SOTTO,
    arti.GRUPPO,
    arti.MISU;


####################################################################################################
CREATE OR REPLACE VIEW `itemsview`
AS
SELECT
    arti.CORTO,
    TRIM(arti.CODI) AS CODI,
    TRIM(arti.CFOR) AS CFOR,
    TRIM(arti.DESCRI) AS DESCRI,
    TRIM(arti.IVA) AS IVA,
    arti.COST,
    arti.VEND,
    TRIM(arti.TIPO) AS TIPO,
    TRIM(arti.TIPO_SOTTO) AS TIPO_SOTTO,
    TRIM(arti.REPA) AS REPA,
    TRIM(arti.REPA_SOTTO) AS REPA_SOTTO,
    TRIM(arti.GRUPPO) AS GRUPPO,
    TRIM(arti.MISU) AS MISU,
    CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'),  EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'))))  AS EAN_8,
    CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0'), EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0')))) AS EAN_13,
    IFNULL(GROUP_CONCAT(TRIM(art_alt.COD_ALT), '|'), '') AS EAN
FROM arti
LEFT JOIN (
    SELECT
        CORTO,
        COD_ALT,
        ROW_NUMBER() OVER (PARTITION BY CORTO ORDER BY N_PRI) AS rn
    FROM
        art_alt
) AS art_alt ON arti.CORTO = art_alt.CORTO
CROSS JOIN (
    SELECT TRIM(VARIA) AS VARIA FROM costanti WHERE NUME = 20
) AS costanti
GROUP BY
    arti.CORTO,
    arti.CODI,
    arti.CFOR,
    arti.DESCRI,
    arti.IVA,
    arti.COST,
    arti.VEND,
    arti.TIPO,
    arti.TIPO_SOTTO,
    arti.REPA,
    arti.REPA_SOTTO,
    arti.GRUPPO,
    arti.MISU;



####################################################################################################
#NOT USED
/*
CREATE FUNCTION Ean8LastDigit(partEan VARCHAR(7)) RETURNS CHAR CHARSET latin1
READS SQL DATA
BEGIN

    DECLARE x INT DEFAULT 0;
    DECLARE a INT DEFAULT 0;
    DECLARE b INT DEFAULT 0;
    DECLARE lastDigit CHAR(1) DEFAULT '';

    IF LENGTH(partEan) <> 7 THEN
        RETURN lastDigit;
    END IF;

    SET x = 2;
    WHILE x <= 4 DO
        SET x = x + 2;
        SET a = a + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
    END WHILE;

    SET a = a * 3;

    SET x = 1;
    WHILE x <= 5 DO
        SET x = x + 2;
        SET b = b + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
    END WHILE;

    SET a = a + b;
    SET b = CAST((a / 10) AS SIGNED);
    SET b = (b * 10) + 10;
    SET a = b - a;

    IF a <> 10 THEN
        SET lastDigit = CAST(LEFT(a,1) AS CHAR);
    ELSE
        SET lastDigit = '0';
    END IF;

    RETURN lastDigit;
END;
*/