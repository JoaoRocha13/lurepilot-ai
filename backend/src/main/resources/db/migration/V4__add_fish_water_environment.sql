alter table fish_species
    add column water_environment varchar(20);

update fish_species
set water_environment = 'FRESHWATER'
where water_environment is null;

alter table fish_species
    alter column water_environment set not null;

create index idx_fish_species_water_environment
    on fish_species (water_environment);
