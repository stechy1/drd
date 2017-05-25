#!/usr/bin/env python

from src.item_builder import ItemBuilder
import os

java_path = '/../src/main/java/'
cur_dir = os.path.dirname(os.path.abspath(__file__))

builder = ItemBuilder()
builder.item_name('Test')
builder.table('TestTable')
builder.set_firebase(False)
builder.set_firebase_child_path('firebase/child/path')
builder.columns([
  {'name': 'id2', 'type': 'int', 'add_snapshot': True},
  {'name': 'name2', 'type': 'String', 'add_snapshot': False},
  {'name': 'downloaded2', 'type': 'boolean', 'add_snapshot': False},
  {'name': 'blob_type2', 'type': 'blob', 'specialized_type': 'byte[]', 'add_snapshot': True},
])

builder.build(cur_dir + java_path)