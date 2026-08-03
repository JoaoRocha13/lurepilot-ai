alter table fish_species
    alter column image_url type text;

alter table lure_library_items
    alter column image_url type text;

alter table lure_library_items
    add column action_icon_url text;

alter table lure_library_items
    add column action_image_url text;
