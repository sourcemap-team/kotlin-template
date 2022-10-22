insert into inconnect_schema.role
    (created_at, last_modified_at, role_name, role_display_name, invite)
values (now(), now(), 'GUEST', 'Guest', false) on conflict do nothing;

insert into inconnect_schema.role
    (created_at, last_modified_at, role_name, role_display_name, invite)
values (now(), now(), 'ADMIN', 'Admin', true) on conflict do nothing;