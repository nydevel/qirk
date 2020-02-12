curl --user elastic -X DELETE -H 'Content-Type: application/json' http://127.0.0.1:9200/user -d ''
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/user -d '{
	"mappings": {
		"properties": {
			"username": {
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"full_name": {
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"name_search_field": {
				"type": "text",
				"index": true,
				"index_options": "positions",
				"index_prefixes": {
					"min_chars" : 1,
					"max_chars" : 19
				},
				"doc_values": false
			},
			"tags": {
				"type": "text",
				"index": true,
				"index_options": "docs",
				"doc_values": false
			},
			"projects": {
				"type": "nested",
				"properties": {
					"project_id": {
						"type": "long",
						"index": true,
						"doc_values": true
					},
					"member_id": {
						"type": "long",
						"index": false,
						"doc_values": true
					}
				}
			},
			"invited_projects": {
				"type": "long",
				"index": true,
				"doc_values": true
			}
		}
	}
}'

curl --user elastic -X DELETE -H 'Content-Type: application/json' http://127.0.0.1:9200/task -d ''
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/task -d '{
	"mappings": {
		"properties": {
			"project_id": {
				"type": "long",
				"index": true,
				"doc_values": false
			},
			"number": {
				"type": "long",
				"index": false,
				"doc_values": false
			},
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
			"summary": {
				"type": "text",
				"index": false,
				"doc_values": false
			},
			"description": {
				"type": "text",
				"index": true,
				"index_options": "positions",
				"doc_values": false
			},
			"reporter": {
				"type": "long",
				"index": true,
				"doc_values": false
			},
			"assignee": {
				"type": "long",
				"index": true,
				"doc_values": false
			},
			"created_at": {
				"type": "long",
				"index": true,
				"doc_values": true
			},
			"updated_at": {
				"type": "long",
				"index": true,
				"doc_values": true
			},
			"task_type": {
				"type": "keyword",
				"index": true,
				"doc_values": false
			},
			"task_priority": {
				"type": "keyword",
				"index": true,
				"doc_values": true
			},
			"task_status": {
				"type": "keyword",
				"index": true,
				"doc_values": false
			},
			"hashtags": {
				"type": "keyword",
				"index": true,
				"doc_values": false
			},
			"hidden": {
				"type": "boolean",
				"index": true,
				"doc_values": false
			},
			"card": {
				"type": "long",
				"index": true,
				"doc_values": false
			}
		}
	}
}'

curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/_scripts/user_add_project -d '{
	"script": {
		"lang": "painless",
		"source": "ctx._source.projects.add(params);"
 	}
}'

curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/_scripts/user_remove_project -d '{
	"script": {
		"lang": "painless",
		"source": "for ( int i = ctx._source.projects.size() - 1; i >= 0; i -- ) {if ( ctx._source.projects[ i ].member_id == params.member_id) {ctx._source.projects.remove(i);} }"
 	}
}'
