curl --user elastic -X DELETE http://127.0.0.1:9200/user
curl --user elastic -X PUT -H 'Content-Type: application/json' http://127.0.0.1:9200/user -d '{
	"mappings": {
		"properties": {
			"dont_recommend": {
				"type": "boolean",
				"index": true,
				"doc_values": false
			},
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
			"organizations": {
				"type": "nested",
				"properties": {
					"organization_id": {
						"type": "long",
						"index": true,
						"doc_values": true
					},
					"member_id": {
						"type": "long",
						"index": false,
						"doc_values": true
					},
					"enabled": {
						"type": "boolean",
						"index": false,
						"doc_values": true
					}
				}
			},
			"projects": {
				"type": "long",
				"index": true,
				"doc_values": true
			},
			"invited_projects": {
				"type": "long",
				"index": true,
				"doc_values": true
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
				"index": true,
				"doc_values": false
			},
			"name": {
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"ui_id": {
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
			"description": {
				"type": "text",
				"index": true,
				"index_options": "docs",
				"doc_values": false
			},
			"tags": {
				"type": "text",
				"index": true,
				"index_options": "docs",
				"doc_values": false
			},
			"organization_id": {
				"type": "long",
				"index": false,
				"doc_values": false
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
				"index": true,
				"doc_values": false
			},
			"name": {
				"type": "keyword",
				"index": false,
				"doc_values": false
			},
			"ui_id": {
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
			}
		}
	}
}'

curl --user elastic -X DELETE http://127.0.0.1:9200/task
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
			}
		}
	}
}'