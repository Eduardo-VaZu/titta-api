-- Agregar columna para guardar el ID de la transacción de la pasarela de pagos (Stripe)
ALTER TABLE tbl_venta
    ADD COLUMN id_transaccion VARCHAR(255);