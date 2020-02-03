curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/task/_mapping -d '{
	"properties": {
		"number_string": {
			"type": "text",
			"index": true,
			"index_options": "positions",
			"index_prefixes": {
				"min_chars" : 1,
				"max_chars" : 5
			},
			"doc_values": false
		},
		"card": {
			"type": "long",
			"index": true,
			"doc_values": false
		}
	}
}'
