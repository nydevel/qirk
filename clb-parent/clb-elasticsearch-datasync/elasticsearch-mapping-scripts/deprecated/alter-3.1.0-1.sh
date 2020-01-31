curl --user elastic -X DELETE http://127.0.0.1:9200/user
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/user -d '{
	"mappings": {
		"properties": {
			"dont_recommend": {
				"type": "boolean",
				"index": true
			},
			"username": {
				"type": "keyword",
				"index": false
			},
			"full_name": {
				"type": "keyword",
				"index": false
			},
			"name_search_field": {
				"type": "text",
				"index": true,
				"index_options": "positions",
				"index_prefixes": {
					"min_chars" : 1,
					"max_chars" : 19
				}
			},
			"tags": {
				"type": "text",
				"index": true,
				"index_options": "docs"
			},
			"organizations": {
				"type": "nested",
				"properties": {
					"organization_id": {
						"type": "long",
						"index": true
					},
					"member_id": {
						"type": "long",
						"index": false
					},
					"enabled": {
						"type": "boolean",
						"index": false
					}
				}
			}
		}
	}
}'
