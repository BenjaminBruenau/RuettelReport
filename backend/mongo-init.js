db.auth('root', 'example')

db = db.getSiblingDB('ruettelreport')

db.createUser(
    {
        user: "media",
        pwd: "media1234",
        roles: [
            {
                role: "readWrite",
                db: "media"
            }
        ]
    }
);