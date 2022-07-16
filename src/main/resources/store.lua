if (redis.call("exist", KEYS[1]) == 1) then
    local store = tonumber(redis.call("get", KEYS[1]));
    if (store > 0) then
        redis.call("incryby", KEYS[1], -1)
        return store;
    end;
        return -1;
end;
