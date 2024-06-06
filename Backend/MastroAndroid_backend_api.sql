####################################################################################################
CREATE OR REPLACE VIEW `ordersview`
AS
SELECT lis_ordc.NUME                                                AS NUME,
       lis_ordc.CODI                                                AS CODI,
       IFNULL(TRIM(clienti.DESCRI), '')                             AS RAGIONESOCIALE,
       IFNULL(IFNULL(TRIM(destina.VIA), TRIM(clienti.VIA)), '')     AS VIA,
       IFNULL(IFNULL(TRIM(destina.CAP), TRIM(clienti.CAP)), '')     AS CAP,
       IFNULL(IFNULL(TRIM(destina.CITTA), TRIM(clienti.CITTA)), '') AS CITTA,
       IFNULL(IFNULL(TRIM(destina.PROV), TRIM(clienti.PROV)), '')   AS PROV,
       IFNULL(TRIM(clienti.NAZIONE), '')                            AS NAZIONE,
       IFNULL(TRIM(lis_ordc.DESCRI), '')                            AS DESCRI,
       IFNULL(TRIM(destina.DESCRI), '')                             AS DESTINAZIONEDESCRI,
       lis_ordc.N_LAV                                               AS NUMEROLAV,
       lis_ordc.DATAI                                               AS DATAI,
       lis_ordc.AGENTE                                              AS AGENTE,
       lis_ordc.DESTINA                                             AS DESTINA,
       TRIM(lis_ordc.TRASPO)                                        AS TRASPO,
       lis_ordc.COLLI                                               AS COLLI,
       IFNULL(TRIM(lis_ordc.VETTORE), TRIM(vettori.DESCRI))         AS VETTORE,
       lis_ordc.VETT_NUME                                           AS VETTORENUME,
       lis_ordc.PESO                                                AS PESO,
       TRIM(lis_ordc.NUME_ORDI)                                     AS NUMEROORDI,
       lis_ordc.DATA_ORDI                                           AS DATAORDI,
       TRIM(lis_ordc.NOTE)                                          AS NOTE,
       lis_ordc.D_CONSEGNA                                          AS DATACONSEGNA,
       lis_ordc.TASSATIVA                                           AS TASSATIVA,
       lis_ordc.CONSEGNA                                            AS CONSEGNA,
       lis_ordc.STATO_CONS                                          AS STATOCONSEGNA,
       lis_ordc.URGENTE                                             AS URGENTE,
       lis_ordc.PARZIALE                                            AS PARZIALE,
       lis_ordc.NUMERO                                              AS NUMERO

FROM lis_ordc

         LEFT JOIN clienti ON lis_ordc.CODI = clienti.CODI
         LEFT JOIN destina ON lis_ordc.DESTINA = destina.PROG_TUTTO
         LEFT JOIN vettori ON lis_ordc.VETT_NUME = vettori.NUME
WHERE lis_ordc.DATAI >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
  AND NOT lis_ordc.CONSOLI

ORDER BY lis_ordc.DATAI DESC;

####################################################################################################
CREATE OR REPLACE VIEW `clientsview`
AS
SELECT clienti.CODI          AS CODI,
       TRIM(clienti.DESCRI)  AS DESCRI,
       TRIM(clienti.VIA)     AS VIA,
       TRIM(clienti.CAP)     AS CAP,
       TRIM(clienti.CITTA)   AS CITTA,
       TRIM(clienti.PROV)    AS PROV,
       TRIM(clienti.NAZIONE) AS NAZIONE,
       TRIM(clienti.DESC2)   AS DESC2,
       TRIM(clienti.PIVA)    AS PIVA,
       TRIM(clienti.COD_FIS) AS CODFIS
FROM clienti
WHERE !clienti.BLOC
  AND clienti.DESCRI <> ''
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

    IF NOT (LENGTH(partEan) = 12 OR LENGTH(partEan) = 7) THEN
        RETURN lastDigit;
    END IF;

    SET x = 2;
    WHILE x <= LENGTH(partEan) - 2
        DO
            SET x = x + 2;
            SET a = a + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
        END WHILE;

    SET a = a * 3;

    SET x = 1;

    WHILE x <= LENGTH(partEan) - 3
        DO
            SET x = x + 2;
            SET b = b + CAST(SUBSTRING(partEan, x, 1) AS SIGNED);
        END WHILE;

    SET a = a + b;
    SET b = CAST((a / 10) AS SIGNED);
    SET b = (b * 10) + 10;
    SET a = b - a;

    IF a <> 10 THEN
        SET lastDigit = CAST(LEFT(a, 1) AS CHAR);
    ELSE
        SET lastDigit = '0';
    END IF;

    RETURN lastDigit;
END;


####################################################################################################
CREATE OR REPLACE VIEW `articlesview`
AS
SELECT arti.CORTO                                                                                                      AS CORTO,
       TRIM(arti.CODI)                                                                                                 AS CODI,
       TRIM(arti.CFOR)                                                                                                 AS CFOR,
       TRIM(arti.DESCRI)                                                                                               AS DESCRI,
       TRIM(arti.IVA)                                                                                                  AS IVA,
       arti.COST                                                                                                       AS COST,
       arti.VEND                                                                                                       AS VEND,
       TRIM(arti.TIPO)                                                                                                 AS TIPO,
       TRIM(arti.TIPO_SOTTO)                                                                                           AS TIPO_SOTTO,
       TRIM(arti.REPA)                                                                                                 AS REPA,
       TRIM(arti.REPA_SOTTO)                                                                                           AS REPA_SOTTO,
       TRIM(arti.GRUPPO)                                                                                               AS GRUPPO,
       TRIM(arti.MISU)                                                                                                 AS MISU,
    /*CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'),  EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'))))*/''  AS EAN_8,
    /*CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0'), EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0'))))*/'' AS EAN_13,
    /*IFNULL(GROUP_CONCAT(TRIM(art_alt.COD_ALT), '|'), '')*/''                                                         AS EAN_ALT
FROM arti
/*LEFT JOIN (
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
    arti.MISU*/;


####################################################################################################
CREATE OR REPLACE VIEW `itemsview`
AS
SELECT arti.CORTO,
       TRIM(arti.CODI)                                                      AS CODI,
       TRIM(arti.CFOR)                                                      AS CFOR,
       TRIM(arti.DESCRI)                                                    AS DESCRI,
       TRIM(arti.IVA)                                                       AS IVA,
       arti.COST,
       arti.VEND,
       TRIM(arti.TIPO)                                                      AS TIPO,
       TRIM(arti.TIPO_SOTTO)                                                AS TIPO_SOTTO,
       TRIM(arti.REPA)                                                      AS REPA,
       TRIM(arti.REPA_SOTTO)                                                AS REPA_SOTTO,
       TRIM(arti.GRUPPO)                                                    AS GRUPPO,
       TRIM(arti.MISU)                                                      AS MISU,
       CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'),
              EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 5, '0'))))  AS EAN_8,
       CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0'),
              EanLastDigit(CONCAT(TRIM(VARIA), LPAD(arti.CORTO, 10, '0')))) AS EAN_13,
       IFNULL(GROUP_CONCAT(TRIM(art_alt.COD_ALT), '|'), '')                 AS EAN
FROM arti
         LEFT JOIN (SELECT CORTO,
                           COD_ALT,
                           ROW_NUMBER() OVER (PARTITION BY CORTO ORDER BY N_PRI) AS rn
                    FROM art_alt) AS art_alt ON arti.CORTO = art_alt.CORTO
         CROSS JOIN (SELECT TRIM(VARIA) AS VARIA
                     FROM costanti
                     WHERE NUME = 20) AS costanti
GROUP BY arti.CORTO,
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

####################################################################################################
-- Modify the rig_ordc table, the column REPA should be the same as the column REPA in the arti table
ALTER TABLE rig_ordc
    MODIFY COLUMN REPA VARCHAR(10) DEFAULT NULL;

####################################################################################################
### Insert a new row into the rig_ordc table, used by the OrderBarcodeReader procedure
DROP PROCEDURE IF EXISTS InsertRowIntoRigOrdC;
CREATE PROCEDURE InsertRowIntoRigOrdC(
    IN orderId INT,
    IN articleId INT,
    IN quantity DECIMAL(11, 5),
    IN orderedQuantity DECIMAL(11, 5),
    IN batch VARCHAR(255),
    IN expiryDate DATE
)
BEGIN
    DECLARE lastRow INT;
    DECLARE docDate DATE;
    DECLARE docNumber INT;
    DECLARE clientId INT;
    DECLARE agentId INT;
    DECLARE agentPerc1 DECIMAL(11, 5);
    DECLARE agentPerc2 DECIMAL(11, 5);

    DECLARE articleCode VARCHAR(100);
    DECLARE articleSupplierCode VARCHAR(100);
    DECLARE articleDescription VARCHAR(255);
    DECLARE articlePrice DECIMAL(11, 5);
    DECLARE articleDiscount1 DECIMAL(11, 5);
    DECLARE articleDiscount2 DECIMAL(11, 5);
    DECLARE articleCost DECIMAL(11, 5);
    DECLARE articleVat VARCHAR(100);
    DECLARE articleVatPercentage DECIMAL(11, 5);
    DECLARE articleDiscount DECIMAL(11, 5);
    DECLARE articleUnitOfMeasure VARCHAR(255);
    DECLARE articleCounterParty VARCHAR(255);
    DECLARE articleSector VARCHAR(255);
    DECLARE articleDepartment VARCHAR(255);
    DECLARE articleQuantityPerPackage DECIMAL(11, 5);
    DECLARE articlePosVat INT;
    DECLARE articleListPrice DECIMAL(11, 5);

    -- Find the last row number for the given order
    SELECT IFNULL(MAX(RIGA), 0) INTO lastRow FROM rig_ordc WHERE NUME = orderId;

    -- Retrieve DOC_DATA, DOC_NUME, and CODI based on the order ID
    SELECT lis_ordc.DATAI,
           lis_ordc.NUMERO,
           lis_ordc.CODI,
           lis_ordc.AGENTE,
           rig_ordc.PROV_1,
           rig_ordc.PROV_2
    INTO docDate, docNumber, clientId, agentId, agentPerc1, agentPerc2
    FROM lis_ordc
             LEFT JOIN rig_ordc ON lis_ordc.NUME = rig_ordc.NUME
    WHERE lis_ordc.NUME = orderId
    LIMIT 1;

    -- Retrieve article data based on the article ID
    SELECT arti.CODI,
           arti.CFOR,
           TRIM(arti.DESCRI),
           CASE
               WHEN arti.CORTO IN (SELECT CORTO FROM clienti_pre WHERE CODI = clientId)
                   THEN (SELECT PREZZO FROM clienti_pre WHERE CODI = clientId AND CORTO = arti.CORTO)
               WHEN arti.CORTO IN (SELECT art_lis.CORTO
                                   FROM art_lis
                                   WHERE
                                       art_lis.NUME = (SELECT clienti.LIST FROM clienti WHERE CODI = clientId LIMIT 1))
                   THEN (SELECT art_lis.VEND
                         FROM art_lis
                         WHERE art_lis.CORTO = arti.CORTO
                           AND art_lis.NUME = (SELECT clienti.LIST FROM clienti WHERE CODI = clientId))
               ELSE arti.VEND
               END AS 'VEND',
           CASE
               WHEN arti.CORTO IN (SELECT CORTO FROM clienti_pre WHERE CODI = clientId)
                   THEN (SELECT SCON_1 FROM clienti_pre WHERE CODI = clientId AND CORTO = arti.CORTO)
               WHEN arti.CORTO IN (SELECT art_lis.CORTO
                                   FROM art_lis
                                   WHERE art_lis.NUME = (SELECT clienti.LIST FROM clienti WHERE CODI = clientId))
                   THEN (SELECT clienti.SCONTO FROM clienti WHERE clienti.CODI = clientId)
               ELSE arti.SCON_1
               END AS 'SCON_1',
           CASE
               WHEN arti.CORTO IN (SELECT CORTO FROM clienti_pre WHERE CODI = clientId)
                   THEN (SELECT SCON_2 FROM clienti_pre WHERE CODI = clientId AND CORTO = arti.CORTO)
               WHEN arti.CORTO IN (SELECT art_lis.CORTO
                                   FROM art_lis
                                   WHERE art_lis.NUME = (SELECT clienti.LIST FROM clienti WHERE CODI = clientId))
                   THEN (SELECT clienti.SCONTO2 FROM clienti WHERE clienti.CODI = clientId)
               ELSE arti.SCON_2
               END AS 'SCON_2',

           arti.COST,
           arti.IVA,
           iva.PERCE,
           arti.SCON,
           arti.MISU,
           arti.CONTRO,
           arti.SETTORE,
           arti.REPA,
           arti.QT_CONF,
           arti.CASSA,
           arti.LISTINO
    INTO articleCode, articleSupplierCode, articleDescription, articlePrice, articleDiscount1, articleDiscount2,
        articleCost, articleVat, articleVatPercentage, articleDiscount, articleUnitOfMeasure, articleCounterParty,
        articleSector, articleDepartment, articleQuantityPerPackage, articlePosVat, articleListPrice
    FROM arti
             LEFT JOIN iva ON iva.CODICE = arti.IVA
    WHERE arti.CORTO = articleId;

    INSERT INTO rig_ordc (NUME, N_TIPO, DOC_DATA, DOC_NUME, CODI, RIGA, CORTO, ART_CODI, ART_CFOR, DESCRI,
                          QUAN, AGENTE, PROV_1, PROV_2, VEND, COSTO, IVA, IVA_PERC, SCON, SCON_1, SCON_2, SCON_3,
                          LISTINO, MISU, DATA, STAM, COLL, SETTORE, REPA, QT_CONF, REPA_CAS, CONTRO,
                          ORD_QT_ORD, LOTTO, DATA_SCA)
    VALUES (orderId, 6, docDate, docNumber, clientId, lastRow + 1, articleId, articleCode, articleSupplierCode,
            articleDescription, quantity, agentId, agentPerc1, agentPerc2, articlePrice, articleCost, articleVat,
            articleVatPercentage,
            articleDiscount, articleDiscount1, articleDiscount2, 0, articleListPrice, articleUnitOfMeasure,
            CURRENT_DATE(), 1, 1,
            articleSector, articleDepartment, articleQuantityPerPackage, articlePosVat, articleCounterParty,
            orderedQuantity, batch, expiryDate);
END;


####################################################################################################
DROP PROCEDURE IF EXISTS OrderBarcodeReader;
CREATE PROCEDURE OrderBarcodeReader(IN orderId INT, IN scannedCode VARCHAR(255))
BEGIN
    DECLARE articleId VARCHAR(6);
    DECLARE batch VARCHAR(100) DEFAULT '';
    DECLARE rowExists INT;
    DECLARE quantity DECIMAL(11, 5) DEFAULT 1;

    IF LENGTH(scannedCode) = 8 THEN
        -- Extract the first four characters from the scanned code for the article ID
        SET articleId = CAST(SUBSTR(scannedCode, 2, 6) AS SIGNED);
        SET quantity = 1;
    ELSEIF LENGTH(scannedCode) = 20 THEN
        -- Extract the first four characters from the scanned code for the article ID
        SET articleId = CAST(SUBSTR(scannedCode, 1, 5) AS SIGNED);
        -- Extract the batch number from the scanned code
        SET batch = TRIM(REPLACE(REPLACE(SUBSTR(scannedCode, 6, 15), 'x', ''), 'X', ''));
        -- Get the quantity per package for the given article
        CALL GetArticlePackageQuantity(articleId, @quantity);
        IF @quantity > 0 THEN
            SET quantity = @quantity;
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Barcode non valido', MYSQL_ERRNO = 5400;
    END IF;

    -- Check if a row with the given conditions exists
    IF LENGTH(scannedCode) = 8 THEN
        SELECT COUNT(*)
        INTO rowExists
        FROM rig_ordc
        WHERE NUME = orderId
          AND CORTO = articleId;
    ELSEIF LENGTH(scannedCode) = 20 THEN
        SELECT COUNT(*)
        INTO rowExists
        FROM rig_ordc
        WHERE NUME = orderId
          AND CORTO = articleId
          AND LOTTO = batch;
    END IF;

    -- If the row does not exist, insert a new row into the rig_ordc table
    IF rowExists = 0 THEN
        CALL InsertRowIntoRigOrdC(orderId, articleId, quantity, 1, batch, NULL);
    ELSE
        -- Update the row in the rig_ordc table with the scanned code
        IF LENGTH(scannedCode) = 8 THEN
            UPDATE rig_ordc
            SET QUAN = QUAN + quantity
            WHERE NUME = orderId
              AND CORTO = articleId;
        ELSEIF LENGTH(scannedCode) = 20 THEN
            UPDATE rig_ordc
            SET QUAN = QUAN + quantity
            WHERE NUME = orderId
              AND CORTO = articleId
              AND LOTTO = batch;
        END IF;
    END IF;

END;

####################################################################################################
DROP PROCEDURE IF EXISTS GetArticlePackageQuantity;
CREATE PROCEDURE GetArticlePackageQuantity(IN articleId INT, OUT packageQuantity DECIMAL(11, 5))
BEGIN
    SELECT QT_CONF INTO packageQuantity FROM arti WHERE CORTO = articleId;

    IF packageQuantity IS NULL THEN
        SET packageQuantity = 0;
    END IF;
END;

####################################################################################################
DROP PROCEDURE IF EXISTS InsertArticleIntoDocument;
CREATE PROCEDURE InsertArticleIntoDocument(
    IN documentId INT,
    IN documentType VARCHAR(20),
    IN articleId INT
)
BEGIN
    IF TRIM(documentType) = 'order' THEN
        CALL InsertRowIntoRigOrdC(documentId, articleId, 1, 1, '', NULL);
    END IF;

END;


####################################################################################################
DROP PROCEDURE IF EXISTS DuplicateOrderRow;
CREATE PROCEDURE DuplicateOrderRow(
    IN orderDetailId INT
)
BEGIN

    DECLARE rowNumber INT;
    DECLARE orderId INT;
    SET rowNumber = (SELECT RIGA FROM rig_ordc WHERE NUME_PRO = orderDetailId);
    SET orderId = (SELECT NUME FROM rig_ordc WHERE NUME_PRO = orderDetailId LIMIT 1);


    START TRANSACTION;

    -- First update RIGA column for all rows after the selected row
    UPDATE rig_ordc SET RIGA = RIGA + 1 WHERE RIGA > rowNumber AND NUME = orderId;

    -- Insert a new row into the rig_ordc table with the same details as the selected row
    INSERT INTO rig_ordc (NUME, N_TIPO, DOC_DATA, DOC_NUME, DOC_BARRA, CODI, NUME_DIS, RIGA, ELABORATA, CORTO, ART_CODI,
                          ART_CFOR, DESCRI, QUAN, PROV_1, PROV_2, AGENTE, COLLI, PESO, VEND, COSTO, IVA,
                          IVA_PERC, SCON, SCON_1, SCON_2, LISTINO, MISU, DATA, STAM, SELE, LIBE, COLL, SETTORE, REPA,
                          QT_CONF, REPA_CAS, CONAI_L, CONAI, ESE_CONAI, CONTRO, ORD_QT_ORD, ORD_QT_CON, DESTINA, LOTTO,
                          DATA_SCA, RAEE, DATA_LOTTO, PADRE_DIST, EXTRA)
    SELECT NUME,
           N_TIPO,
           DOC_DATA,
           DOC_NUME,
           DOC_BARRA,
           CODI,
           NUME_DIS,
           RIGA + 1,
           ELABORATA,
           CORTO,
           ART_CODI,
           ART_CFOR,
           DESCRI,
           QUAN,
           PROV_1,
           PROV_2,
           AGENTE,
           COLLI,
           PESO,
           VEND,
           COSTO,
           IVA,
           IVA_PERC,
           SCON,
           SCON_1,
           SCON_2,
           LISTINO,
           MISU,
           DATA,
           STAM,
           SELE,
           LIBE,
           COLL,
           SETTORE,
           REPA,
           QT_CONF,
           REPA_CAS,
           CONAI_L,
           CONAI,
           ESE_CONAI,
           CONTRO,
           IF(ORD_QT_ORD - ORD_QT_CON - QUAN > 0, ORD_QT_ORD - ORD_QT_CON - QUAN, 0) AS ORD_QT_ORD,
           0                                                                         AS ORD_QT_CON,
           DESTINA,
           ''                                                                        AS LOTTO,
           NULL                                                                      AS DATA_SCA,
           RAEE,
           NULL                                                                      AS DATA_LOTTO,
           PADRE_DIST,
           EXTRA
    FROM rig_ordc
    WHERE NUME_PRO = orderDetailId
    LIMIT 1;

    -- Update the quantity of the selected row
    UPDATE rig_ordc SET ORD_QT_ORD = QUAN WHERE NUME_PRO = orderDetailId;
    COMMIT;

    SELECT * FROM ordersview WHERE NUME = (SELECT NUME FROM rig_ordc WHERE RIGA = orderDetailId);

END;

####################################################################################################
DROP PROCEDURE IF EXISTS ModifyOrderDeliveryState;
CREATE PROCEDURE ModifyOrderDeliveryState(
    IN orderId INT,
    IN deliveryState INT
)
BEGIN
    UPDATE lis_ordc
    SET STATO_CONS = deliveryState
    WHERE NUME = orderId;

    SELECT * FROM ordersview WHERE NUME = orderId;

END;

####################################################################################################
DROP PROCEDURE IF EXISTS UpdateOrderRow;
CREATE PROCEDURE UpdateOrderRow(
    IN orderDetailId INT,
    IN quantity DECIMAL(11, 5),
    IN batch VARCHAR(255),
    IN expirationDate VARCHAR(255)
)
BEGIN
    DECLARE updatedDate DATE;
    IF expirationDate IS NULL OR expirationDate = '' OR expirationDate = 'null' THEN
        SET expirationDate = NULL;
    ELSE
        SET updatedDate = STR_TO_DATE(expirationDate, '%d/%m/%Y');
    END IF;

    IF updatedDate IS NOT NULL THEN
        SET expirationDate = updatedDate;
    ELSE
        SET expirationDate = NULL;
    END IF;

    UPDATE rig_ordc
    SET QUAN     = quantity,
        LOTTO    = batch,
        DATA_SCA = expirationDate
    WHERE NUME_PRO = orderDetailId;

    SELECT * FROM ordersview WHERE NUME = (SELECT NUME FROM rig_ordc WHERE NUME_PRO = orderDetailId);

END;

####################################################################################################
DROP PROCEDURE IF EXISTS RigOrdcFilteredList;
CREATE PROCEDURE RigOrdcFilteredList(
    IN orderId INT
)
BEGIN
    SELECT *
    FROM rig_ordc
    WHERE NUME = orderId
    ORDER BY RIGA;
END;

####################################################################################################
CREATE OR REPLACE VIEW clientsdestinationsview
AS
(
SELECT destina.PROG_TUTTO   AS PROG_TUTTO,
       destina.CODI         AS CODI,
       TRIM(destina.DESCRI) AS DESCRI,
       TRIM(destina.VIA)    AS VIA,
       TRIM(destina.CAP)    AS CAP,
       TRIM(destina.CITTA)  AS CITTA,
       TRIM(destina.PROV)   AS PROV
FROM destina
WHERE destina.CODI IN (SELECT CODI FROM clientsview));

####################################################################################################
CREATE VIEW test as
(
select clienti.*, destina.DESCRI AS destina_d, destina.citta as destina_c
from clienti
         left join destina on clienti.CODI = destina.CODI);

####################################################################################################
DROP PROCEDURE IF EXISTS InsertNewOrder;
CREATE PROCEDURE InsertNewOrder(
    IN clientId INT,
    IN destinationId INT,
    IN description VARCHAR(255),
    IN insertDate VARCHAR(255),
    IN deliveryDate VARCHAR(255)
)
BEGIN

    DECLARE lastOrderId INT;
    DECLARE insertDateAsDate DATE;
    DECLARE deliveryDateAsDate DATE;
    DECLARE orderNumber INT;

    IF insertDate IS NULL OR insertDate = '' OR insertDate = 'null' THEN
        SET insertDate = CURDATE();
    ELSE
        SET insertDateAsDate = STR_TO_DATE(insertDate, '%d/%m/%Y');
    END IF;

    IF deliveryDate IS NULL OR deliveryDate = '' OR deliveryDate = 'null' THEN
        SET deliveryDateAsDate = NULL;
    ELSE
        SET deliveryDateAsDate = STR_TO_DATE(deliveryDate, '%d/%m/%Y');
    END IF;

    SELECT MAX(NUMERO) + 1 INTO orderNumber FROM lis_ordc WHERE YEAR(DATAI) = YEAR(insertDateAsDate);

    INSERT INTO lis_ordc (CODI, DESTINA, DESCRI, DATAI, D_CONSEGNA, NUMERO)
    VALUES (clientId, destinationId, description, insertDateAsDate, deliveryDateAsDate, orderNumber);

    SELECT NUME
    INTO lastOrderId
    FROM lis_ordc
    WHERE CODI = clientId
      AND DESTINA = destinationId
      AND DESCRI = description
      AND DATAI = insertDateAsDate
      # AND D_CONSEGNA = deliveryDateAsDate # This condition is not necessary because the delivery date can be NULL
      AND NUMERO = orderNumber
    ORDER BY NUME DESC
    LIMIT 1;

    -- Return the last order details based on the last order number
    SELECT * FROM ordersview WHERE NUME = lastOrderId LIMIT 1;
END;


####################################################################################################

-- Create a new user
CREATE USER 'bogdan'@'%' IDENTIFIED BY '85000aab';
GRANT ALL PRIVILEGES ON *.* TO 'bogdan'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

CREATE USER 'apptest'@'%' IDENTIFIED BY 'apptest';
GRANT ALL PRIVILEGES ON *.* TO 'apptest'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

CREATE USER 'android'@'%' IDENTIFIED BY 'android';
GRANT ALL PRIVILEGES ON *.* TO 'android'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;


CREATE USER 'thomas'@'%' IDENTIFIED BY 'thomas';
GRANT ALL PRIVILEGES ON *.* TO 'thomas'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;


# Example of handling an error in a stored procedure
#SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Login failed', MYSQL_ERRNO = 5400;