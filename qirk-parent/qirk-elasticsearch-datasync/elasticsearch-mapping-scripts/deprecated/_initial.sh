curl --user elastic -X PUT http://127.0.0.1:9200/_settings -H "Content-Type: application/json" -d '{ "index": { "blocks": { "read_only_allow_delete": "false" } } }'

curl --user elastic -X DELETE http://127.0.0.1:9200/user
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/user -d '{
	"mappings": {
		"properties": {
			"dont_recommend": {
				"type": "boolean",
				"index": true
			},
			"alias": {
				"type": "keyword",
				"index": false
			},
			"first_name": {
				"type": "keyword",
				"index": false
			},
			"last_name": {
				"type": "keyword",
				"index": false
			},
			"identity_search_field": {
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

curl --user elastic -X DELETE http://127.0.0.1:9200/project
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/project -d '{
	"mappings": {
		"properties": {
			"private": {
				"type": "boolean",
				"index": true
			},
			"name": {
				"type": "keyword",
				"index": false
			},
			"ui_id": {
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
			"description": {
				"type": "text",
				"index": true,
				"index_options": "docs"
			},
			"tags": {
				"type": "text",
				"index": true,
				"index_options": "docs"
			},
			"organization_id": {
				"type": "long",
				"index": false
			}
		}
	}
}'

curl --user elastic -X DELETE http://127.0.0.1:9200/organization
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/organization -d '{
	"mappings": {
		"properties": {
			"private": {
				"type": "boolean",
				"index": true
			},
			"name": {
				"type": "keyword",
				"index": false
			},
			"ui_id": {
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
			}
		}
	}
}'
