-- DB: lemontree

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS member;

CREATE TABLE `member`
(
    member_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(30)    NOT NULL,
    balance            DECIMAL(12, 0) NOT NULL,
    once_limit         DECIMAL(12, 0) NOT NULL,
    daily_limit        DECIMAL(12, 0) NOT NULL,
    monthly_limit      DECIMAL(12, 0) NOT NULL,
    daily_accumulate   DECIMAL(12, 0) NOT NULL,
    monthly_accumulate DECIMAL(12, 0) NOT NULL,
    is_deleted         BOOLEAN        NOT NULL DEFAULT FALSE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `payment`
(
    payment_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id           BIGINT         NOT NULL,
    payment_amount      DECIMAL(12, 0) NOT NULL,
    payment_status      VARCHAR(10)    NOT NULL,
    payback_amount      DECIMAL(12, 0) NOT NULL,
    payback_status      VARCHAR(10)    NOT NULL,
    payment_approved_at DATETIME       NULL,
    payment_canceled_at DATETIME       NULL,
    payback_approved_at DATETIME       NULL,
    payback_canceled_at DATETIME       NULL,

    FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    INDEX idx_payment_member_id (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

