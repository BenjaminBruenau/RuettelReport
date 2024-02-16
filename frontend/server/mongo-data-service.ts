import { MongoClient } from "mongodb"


const projectSettingsCollectionPrefix = 'project_settings_'
const analysisReportCollectionPrefix = 'ruettel_report_'
const analyticsCompleteCollectionSuffix = '_complete'
const analyticsCompleteMagDistributionCollectionSuffix = '_complete_magdistr'

class MongoDataService {
    uri
    client
    db
    constructor() {
        this.uri = useRuntimeConfig().mongoConnectionUri
        this.client = new MongoClient(this.uri)
        this.db = this.client.db("ruettelreport")
    }

    async getRealtimeAnalysisResultsPerQuery(tenantId: string, userId: string) {
        // Only return relevant data for that user of the specific Tenant
        const filter = { tenantId: tenantId, userId: userId}
        const aggregations = this.db.collection(
            'analytics_magdistr').findOne(filter, { sort: { _id: -1 } })
        const magDistribution = this.db.collection(
            'analytics_magdistr').findOne(filter, { sort: { _id: -1 } })

        return Promise.all([aggregations, magDistribution])
    }

    async getRealtimeAnalysisResultsTotal(tenantId: string, userId: string) {
        const filter = { tenantId: tenantId, userId: userId}
        const aggregations = this.db.collection(
            'analytics' + analyticsCompleteCollectionSuffix).findOne(filter, { sort: { _id: -1 } })
        const magDistribution = this.db.collection(
            'analytics' + analyticsCompleteMagDistributionCollectionSuffix).findOne(filter, { sort: { _id: -1 } })

        return Promise.all([aggregations, magDistribution])
    }
    async getLastRuettelReport(tenantId: string) {
        // returns the last inserted ruettel report document
        return this.db.collection(analysisReportCollectionPrefix + tenantId).findOne({}, { sort: { _id: -1 } })
    }

    async getRuettelReports(tenantId: string) {
        return this.convertCursor(this.db.collection(analysisReportCollectionPrefix + tenantId).find({}, { sort: { _id: -1 } }))
    }

    async getRuettelReportsWithPagination(tenantId: string, pageNumber: number, rows: number) {
        return this.getWithPagination(analysisReportCollectionPrefix + tenantId, pageNumber, rows)
    }

    async saveProjectSettings(tenantId: string, settingsDocument: any) {
        return this.db.collection(projectSettingsCollectionPrefix + tenantId).insertOne(settingsDocument)
    }

    async getProjectSettings(tenantId: string) {
        // returns the last inserted project settings document
        return this.db.collection(projectSettingsCollectionPrefix + tenantId).findOne({}, { sort: { _id: -1 } })
    }

    async getProjectSettingsWithPagination(tenantId: string, pageNumber: number, rows: number) {
        return this.getWithPagination(projectSettingsCollectionPrefix + tenantId, pageNumber, rows)
    }


    async listAllProjectSettings(tenantId: string) {
        const collection = this.db.collection(projectSettingsCollectionPrefix + tenantId)
        const result: any[] = []

        const cursor = collection.find()
        for await (const document of cursor) {
            result.push(document)
        }
        return result
    }

    async countProjectSettings(tenantId: string): Promise<number> {
        const collection = this.db.collection(projectSettingsCollectionPrefix + tenantId)
        return collection.countDocuments()
    }


    async getWithPagination(collectionName: string, pageNumber: number, rows: number) {
        const collection = this.db.collection(collectionName)
        if (pageNumber === 0) {
            const cursor = collection.find().limit(rows)
            return this.convertCursor(cursor)
        }
        const cursor = collection.find().skip(pageNumber * rows).limit(rows)
        return this.convertCursor(cursor)
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