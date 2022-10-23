insert into backend_kotlin_template.role
    (created_at, last_modified_at, role_name, role_display_name, invite, provide)
values (now(), now(), 'GUEST', 'Guest', false, false) on conflict do nothing;

insert into backend_kotlin_template.role
    (created_at, last_modified_at, role_name, role_display_name, invite, provide)
values (now(), now(), 'ADMIN', 'Admin', true, true) on conflict do nothing;