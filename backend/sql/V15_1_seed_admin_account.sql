USE offer_bridge;

INSERT INTO user_account(phone, role, status)
SELECT '19900069999', 'ADMIN', 'ACTIVE'
WHERE NOT EXISTS (
  SELECT 1 FROM user_account WHERE phone = '19900069999'
);

