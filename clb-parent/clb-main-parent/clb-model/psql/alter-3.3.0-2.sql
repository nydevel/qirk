UPDATE user_profile SET email_address = lower(email_address);
UPDATE email_activation_token SET email_address = lower(email_address);
UPDATE project_invite_token SET email_address = lower(email_address);

