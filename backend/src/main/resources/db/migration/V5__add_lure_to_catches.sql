alter table catches add column lure_library_item_id bigint;

alter table catches
    add constraint fk_catches_lure_library_item
    foreign key (lure_library_item_id)
    references lure_library_items (id)
    on delete set null;

create index idx_catches_lure_library_item on catches (lure_library_item_id);
