-- create master table
CREATE TABLE partitioned_table
(
    id              UUID             NOT NULL,
    timestamp       TIMESTAMPTZ      NOT NULL,
    some_data       TEXT
) PARTITION BY RANGE (timestamp);

-- create partitions

-- September 2020
CREATE TABLE partitioned_table_2020_09 PARTITION OF partitioned_table (
    PRIMARY KEY (id)
) FOR VALUES FROM ('2020-09-01 00:00:00') TO ('2020-10-01 00:00:00');

-- October 2020
CREATE TABLE partitioned_table_2020_10 PARTITION OF partitioned_table (
    PRIMARY KEY (id)
) FOR VALUES FROM ('2020-10-01 00:00:00') TO ('2020-11-01 00:00:00');

-- November 2020
CREATE TABLE partitioned_table_2020_11 PARTITION OF partitioned_table (
    PRIMARY KEY (id)
) FOR VALUES FROM ('2020-11-01 00:00:00') TO ('2020-12-01 00:00:00');
