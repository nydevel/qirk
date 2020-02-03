ALTER TABLE organization ADD COLUMN predefined_for_user BOOL;
UPDATE organization SET predefined_for_user = (predefined_for_user_id IS NOT NULL);
ALTER TABLE organization ALTER COLUMN predefined_for_user SET NOT NULL;

ALTER TABLE organization ADD COLUMN owner_user_id BIGINT;
UPDATE organization SET owner_user_id = predefined_for_user_id;
UPDATE organization SET owner_user_id = (
	SELECT user_organization.user_id FROM user_organization WHERE user_organization.organization_id = organization.id ORDER BY user_organization.id ASC LIMIT 1
) WHERE owner_user_id IS NULL;
ALTER TABLE organization ALTER COLUMN owner_user_id SET NOT NULL;

