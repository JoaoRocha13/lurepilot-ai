alter table fishing_spots
    add column spot_type varchar(50) not null default 'OTHER';

update fishing_spots
set spot_type = case
    when lower(name) like '%barragem%'
      or lower(name) like '%albufeira%'
      or lower(name) like '%reservoir%'
      or lower(name) like '%dam%'
        then 'RESERVOIR'
    when lower(name) like '%rio%'
      or lower(name) like '%ribeira%'
      or lower(name) like '%river%'
      or lower(name) like '%stream%'
        then 'RIVER'
    when lower(name) like '%lago%'
      or lower(name) like '%lagoa%'
      or lower(name) like '%lake%'
      or lower(name) like '%lagoon%'
        then 'LAKE'
    when lower(name) like '%mar%'
      or lower(name) like '%sea%'
      or lower(name) like '%costa%'
      or lower(name) like '%coast%'
      or lower(name) like '%praia%'
      or lower(name) like '%beach%'
      or lower(name) like '%porto%'
      or lower(name) like '%harbor%'
      or lower(name) like '%marina%'
        then 'COAST'
    else 'OTHER'
end;

create index idx_spots_spot_type on fishing_spots (spot_type);
