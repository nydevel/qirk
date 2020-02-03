curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/task/_mapping -d '{
	"properties": {
		"hidden": {
			"type": "boolean",
			"index": true,
			"doc_values": false
		}
	}
}'
