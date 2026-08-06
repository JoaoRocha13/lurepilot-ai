alter table weather_snapshots
    add column if not exists source_location_id integer,
    add column if not exists current_temperature double precision,
    add column if not exists apparent_temperature double precision,
    add column if not exists relative_humidity double precision,
    add column if not exists precipitation double precision,
    add column if not exists pressure_msl double precision,
    add column if not exists cloud_cover integer,
    add column if not exists wind_speed_kmh double precision,
    add column if not exists wind_gusts_kmh double precision,
    add column if not exists sunrise varchar(255),
    add column if not exists sunset varchar(255),
    add column if not exists hourly_forecast_json text;

drop index if exists idx_weather_location;

create index if not exists idx_weather_location on weather_snapshots(source_location_id);
