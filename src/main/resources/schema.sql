DROP TABLE IF EXISTS member;
CREATE TABLE member
(
    member_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(30) NOT NULL,
    money      BIGINT      NOT NULL,
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS payment;
CREATE TABLE payment
(
    payment_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT  NOT NULL,
    payment_money  BIGINT  NOT NULL,
    payment_status TINYINT NOT NULL,
    payback_money  BIGINT  NULL,
    payback_status TINYINT NULL,
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    INDEX idx_member_id (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;