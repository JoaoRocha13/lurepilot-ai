update fishing_spots
set spot_type = case
    when lower(name) like '%estuari%'
      or lower(name) like '%estuary%'
      or lower(name) like '%foz%'
      or lower(name) like '%river mouth%'
        then 'ESTUARY'
    when lower(name) like '%porto%'
      or lower(name) like '%marina%'
      or lower(name) like '%cais%'
      or lower(name) like '%doca%'
      or lower(name) like '%harbor%'
      or lower(name) like '%harbour%'
        then 'HARBOR'
    else spot_type
end
where lower(name) like '%estuari%'
   or lower(name) like '%estuary%'
   or lower(name) like '%foz%'
   or lower(name) like '%river mouth%'
   or lower(name) like '%porto%'
   or lower(name) like '%marina%'
   or lower(name) like '%cais%'
   or lower(name) like '%doca%'
   or lower(name) like '%harbor%'
   or lower(name) like '%harbour%';
