CREATE TABLE tb_cliente (
	id bigint,
	nome varchar(50) NOT NULL,
	cpf bigint NOT NULL,
	tel bigint NOT NULL,
	cep varchar(9) NOT NULL,
	-- NOVO
	endereco varchar(50) NOT NULL,
	numero bigint NOT NULL,
	cidade varchar(50) NOT NULL,
	estado varchar(50) NOT NULL,
	CONSTRAINT pk_id_cliente PRIMARY KEY(id)
);

CREATE TABLE tb_produto(
	id bigint,
	codigo varchar(10) NOT NULL,
	nome varchar(50) NOT NULL,
	modelo varchar(50) NOT NULL,
	-- NOVO
	descricao varchar(100) NOT NULL,
	valor numeric(10, 2) NOT NULL,
	CONSTRAINT pk_id_produto PRIMARY KEY(id)
);

CREATE TABLE tb_venda(
	id bigint,
	codigo varchar(10) NOT NULL,
	id_cliente_fk bigint NOT NULL,
	valor_total numeric(10, 2) NOT NULL,
	data_venda TIMESTAMPTZ NOT NULL,
	status_venda varchar(50) NOT NULL,
	CONSTRAINT pk_id_venda PRIMARY KEY(id),
	CONSTRAINT fk_id_cliente_venda FOREIGN KEY(id_cliente_fk) REFERENCES tb_cliente(id)
);

CREATE TABLE tb_produto_quantidade(
	id bigint,
	id_produto_fk bigint NOT NULL,
	id_venda_fk bigint NOT NULL,
	quantidade int NOT NULL,
	valor_total numeric(10, 2) NOT NULL,
	CONSTRAINT pk_id_prod_venda PRIMARY KEY(id),
	CONSTRAINT fk_id_prod_venda FOREIGN KEY(id_produto_fk) REFERENCES tb_produto(id),
	CONSTRAINT fk_id_prod_venda_venda FOREIGN KEY(id_venda_fk) REFERENCES tb_venda(id)
);

CREATE sequence sq_cliente START 1 increment 1 owned by tb_cliente.id;

CREATE sequence sq_produto START 1 increment 1 owned by tb_produto.id;

CREATE sequence sq_venda START 1 increment 1 owned by tb_venda.id;

CREATE sequence sq_produto_quantidade START 1 increment 1 owned by tb_produto_quantidade.id;

ALTER TABLE
	TB_CLIENTE
ADD
	CONSTRAINT UK_CPF_CLIENTE UNIQUE (CPF);

ALTER TABLE
	TB_PRODUTO
ADD
	CONSTRAINT UK_CODIGO_PRODUTO UNIQUE (CODIGO);

ALTER TABLE
	TB_VENDA
ADD
	CONSTRAINT UK_CODIGO_VENDA UNIQUE (CODIGO);

SELECT
	V.ID AS ID_VENDA,
	V.CODIGO,
	V.ID_CLIENTE_FK,
	V.VALOR_TOTAL,
	V.DATA_VENDA,
	V.STATUS_VENDA,
	C.ID AS ID_CLIENTE,
	C.NOME,
	C.CPF,
	C.TEL,
	C.ENDERECO,
	C.NUMERO,
	C.CIDADE,
	C.ESTADO,
	P.ID AS ID_PROD_QTD,
	P.QUANTIDADE,
	P.VALOR_TOTAL AS PROD_QTD_VALOR_TOTAL
FROM
	TB_VENDA V
	INNER JOIN TB_CLIENTE C ON V.ID_CLIENTE_FK = C.ID
	INNER JOIN TB_PRODUTO_QUANTIDADE P ON P.ID_VENDA_FK = V.ID
WHERE
	V.CODIGO = 'A1';

SELECT
	PQ.ID,
	PQ.QUANTIDADE,
	PQ.VALOR_TOTAL,
	P.ID AS ID_PRODUTO,
	P.CODIGO,
	P.NOME,
	P.DESCRICAO,
	P.VALOR
FROM
	TB_PRODUTO_QUANTIDADE PQ
	INNER JOIN TB_PRODUTO P ON P.ID = PQ.ID_PRODUTO_FK;


-- Cria tabela de controle de estoque (1:1 com tb_produto)
CREATE TABLE tb_estoque (
    produto_id INTEGER PRIMARY KEY REFERENCES tb_produto (id) ON DELETE CASCADE,
    quantidade INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_tb_estoque_qte_nao_negativa CHECK (quantidade >= 0)
);

-- Inicializa estoque = 0 para produtos já existentes que ainda não tenham linha em tb_estoque
INSERT INTO tb_estoque (produto_id, quantidade)
SELECT p.id, 0
FROM tb_produto p
LEFT JOIN tb_estoque e ON e.produto_id = p.id
WHERE e.produto_id IS NULL;