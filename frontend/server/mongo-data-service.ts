import { MongoClient } from "mongodb"


class MongoDataService {
    uri
    client
    db
    collection
    constructor() {
        this.uri = useRuntimeConfig().mongoConnectionUri
        this.client = new MongoClient(this.uri)
        this.db = this.client.db("ruettelreport")
        this.collection = this.db.collection("media")
    }



    async insert(document: any) {
        const result = await this.collection.insertOne(document)
    }

    async listAll() {
        const result: any[] = []

        const cursor = this.collection.find()
        for await (const document of cursor) {
            result.push(document)
        }
        return result
    }

    async countMediaFiles(): Promise<number> {
        return this.collection.countDocuments()
    }

    async listWithPagination(pageNumber: number, rows: number) {
        if (pageNumber === 0) {
            const cursor = this.collection.find().limit(rows)
            return this.convertCursor(cursor)
        }
        const cursor = this.collection.find().skip(pageNumber * rows).limit(rows)
        return this.convertCursor(cursor)
    }

    async queryWithPagination(searchTerm: string, pageNumber: number, rows: number) {
        const query = {$or:[
                {"name": new RegExp(searchTerm, 'i')},
                {"content": new RegExp(searchTerm, 'i')},
                {"tags": new RegExp(searchTerm, 'i')},
                {"originalName": new RegExp(searchTerm, 'i')},
            ]}
        const matchingDocumentsCount = await this.collection.countDocuments(query)
        if (pageNumber === 0) {
            const cursor = this.collection.find(query).limit(rows)
            const result = await this.convertCursor(cursor)
            return {count: matchingDocumentsCount, result: result}
        }
        const cursor = this.collection.find(query).skip(pageNumber * rows).limit(rows)
        const result = await this.convertCursor(cursor)
        return {count: matchingDocumentsCount, result: result }
    }

    async queryWithSearchTerm(searchTerm: string) {
        const cursor = this.collection.find(
            {$or:[
                    {"name": new RegExp(searchTerm, 'i')},
                    {"content": new RegExp(searchTerm, 'i')},
                    {"tags": new RegExp(searchTerm, 'i')},
                    {"originalName": new RegExp(searchTerm, 'i')},
                ]}
        )
        const result: any[] = []
        for await (const document of cursor) {
            result.push(document)
        }
        return result
    }

    async convertCursor(cursor: any): Promise<any[]> {
        const result: any[] = []
        for await (const document of cursor) {
            result.push(document)
        }
        return result
    }
}

export default new MongoDataService()