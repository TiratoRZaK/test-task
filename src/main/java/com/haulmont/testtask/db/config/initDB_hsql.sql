create table
    patients (
                 id             bigint          identity    not null,
                 firstName      varchar(45)     not null,
                 lastName       varchar(45)     not null,
                 patronymic     varchar(45),
                 phone          varchar(12),
                 primary key (id)
);
create table
    doctors (
                id              bigint          identity    not null,
                firstName       varchar(45)     not null,
                lastName        varchar(45)     not null,
                patronymic      varchar(45),
                specialization  varchar(45)     not null,
                primary key (id)
);
create table
    recipes (
                id              bigint          identity    not null,
                description     varchar(500)    not null,
                patient_id      bigint          not null,
                doctor_id       bigint          not null,
                date_create     date            not null,
                validityInDay   int             not null,
                priority        varchar(15)     not null,
                primary key     (id),
                foreign key     (patient_id) references patients (id),
                foreign key     (doctor_id)  references doctors (id)
);