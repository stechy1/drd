base_package = 'cz/stechy/drd/'
templates = './resources/templates/'
online_item_template = ''
base_manager_template = ''
advanced_manager_template = ''
getter_setter_property_template = ''
getter_setter_object_property_template = ''
builder_template = ''

with open(templates + 'online_item_template.txt', 'r', encoding="utf8") as online_item_template_file:
  online_item_template = online_item_template_file.read()

with open(templates + 'base_manager_template.txt', 'r', encoding="utf8") as base_manager_template_file:
  base_manager_template = base_manager_template_file.read()

with open(templates + 'advanced_manager_template.txt', 'r', encoding="utf8") as advanced_manager_template_file:
  advanced_manager_template = advanced_manager_template_file.read()

with open(templates + 'getter_setter_property_template.txt', 'r', encoding="utf8") as getter_setter_property_template_file:
  getter_setter_property_template = getter_setter_property_template_file.read()

with open(templates + 'getter_setter_object_property_template.txt', 'r', encoding="utf8") as getter_setter_object_property_template_file:
  getter_setter_object_property_template = getter_setter_object_property_template_file.read()

with open(templates + 'builder_template.txt', 'r', encoding="utf8") as builder_template_file:
  builder_template = builder_template_file.read()

def type_to_class_wrap(type):
  if type == 'int':
    return 'Integer'
  elif type == 'boolean':
    return 'Boolean'
  elif type == 'long':
    return 'Long'
  else:
    return type

def type_to_property(name, type, specialized_type=''):
  property_template = 'protected final {type}Property{specialized_type} {name} = new Simple{type}Property();\n'

  if type == 'int':
    return property_template.format(type='Integer', name=name, specialized_type='')
  elif type == 'String':
    return property_template.format(type='String', name=name, specialized_type='')
  elif type == 'boolean':
    return property_template.format(type='Boolean', name=name, specialized_type='')
  elif type == 'blob':
    return property_template.format(type='Object', name=name, specialized_type='<{}>'.format(specialized_type))
  elif type == 'money':
    return 'protected final Money {name} = new Money();'.format(name=name)

def type_to_query(type):
  if type == 'int':
    return '\n+ "%s INT NOT NULL,"'
  if type == 'String':
    return '\n+ "%s VARCHAR(255) NOT NULL,"'
  if type == 'blob':
    return '\n+ "%s BLOB,"'
  if type == 'boolean':
    return '\n+ "%s BOOLEAN NOT NULL,"'
  else:
    return 'unknown'

def format(what, dic):
  result = what
  for entry in dic:
    result = result.replace('{' + entry['name'] + '}', entry['replace'])
  return result

class ItemBuilder:

  def __init__(self):
    self.table_name = ''
    self.firebase_child_path = ''
    self.use_firebase = False
    self.generic_class = ''
    self.cols = {}

  def table(self, table):
    self.table_name = table

  def set_firebase(self, use):
    self.use_firebase = use

  def set_firebase_child_path(self, path):
    self.firebase_child_path = path

  def item_name(self, name):
    self.generic_class = name

  def columns(self, cols):
    self.cols = cols

  def build(self, source_path):
    def build_item(file):
      class_name = self.generic_class
      properties = ''
      fields_in_constructor = ''
      fields_in_constructor_params = ''
      fields_assignment = ''
      getters_and_setters = ''
      builder_fields = ''
      builder_setters = ''
      builder_constructor_fields = ''

      for col in self.cols:
        name = col['name']
        type = col['type']
        specialized_type = col.get('specialized_type', '')
        title = col['name'].title()
        is_blob = type == 'blob'
        final_type = specialized_type if is_blob else type
        properties += type_to_property(name, type, specialized_type)
        fields_in_constructor += '{} {}, '.format(final_type, name)
        fields_in_constructor_params += 'item.get{name}(), '.format(name=title)
        fields_assignment += 'this.{name}.setValue({name});\n'.format(name=name)
        getters_and_setters_template = getter_setter_object_property_template if is_blob else getter_setter_property_template
        getters_and_setters += format(getters_and_setters_template, [{'name': 'name', 'replace': name}, {'name': 'type', 'replace': type_to_class_wrap(final_type)}, {'name': 'name_title', 'replace': title}])
        builder_fields += 'private {} {};\n'.format(final_type, name)
        builder_setters += format(builder_template, [{'name': 'name', 'replace': name}, {'name': 'type', 'replace': final_type}])
        builder_constructor_fields += '{}, '.format(name)

      content = format(online_item_template, [{'name': 'class_name', 'replace': class_name},
                                                {'name': 'properties', 'replace': properties},
                                                {'name': 'fields_in_constructor', 'replace': fields_in_constructor},
                                                {'name': 'fields_in_constructor_params', 'replace': fields_in_constructor_params},
                                                {'name': 'fields_assignment', 'replace': fields_assignment},
                                                {'name': 'getters_and_setters', 'replace': getters_and_setters},
                                                {'name': 'builder_fields', 'replace': builder_fields},
                                                {'name': 'builder_setters', 'replace': builder_setters},
                                                {'name': 'builder_constructor_fields', 'replace': builder_constructor_fields},
                                                ])
      file.write(content)

    def build_manager(file):
      def build_data_snapshot(name, type):
        def get_class(type):
          if type == "String":
            return "String"
          elif type == "int":
            return "Integer"
          elif type == "blob":
            return "String"
          elif type == 'boolean':
            return "Boolean"

        entry = '.{}('
        if type == 'blob':
          entry += "base64ToBlob("
        entry += 'snapshot.child(COLUMN_{}).getValue({}.class))'
        if type == 'blob':
          entry += ')'
        entry = entry.format(name, name.upper(), get_class(type))
        return entry + '\n'
      def build_result_set(name, type):
        def get_what(type):
          if type == "String":
            return "getString"
          elif type == "int":
            return "getInt"
          elif type == "boolean":
            return "getBoolean"
        entry = '.{}('
        if type == 'blob':
          entry += "readBlob(resultSet, COLUMN_{}))"
          entry = entry.format(name, name.upper())
        else:
          entry += 'resultSet.{}(COLUMN_{}))'
          entry = entry.format(name, get_what(col["type"]), name.upper())
        return entry + '\n'
      column_template = 'private static final String COLUMN_{name_upper} = TABLE + "_{name}";\n'
      generic_class = self.generic_class
      table = self.table_name
      firebase_child_name = self.firebase_child_path
      columns = ""
      column_array = ""
      query_create = ""
      parse_data_snapshot = ""
      parse_result_set = ""
      item_to_params = ""
      for col in self.cols:
        name = col['name']
        type = col['type']
        title = col['name'].title()

        columns += column_template.format(name_upper=name.upper(), name=name)
        column_array += 'COLUMN_{name_upper}, '.format(name_upper=name.upper())
        query_create += type_to_query(type)
        parse_data_snapshot += build_data_snapshot(name, type)
        parse_result_set += build_result_set(name, type)
        item_to_params += 'item.get{}(),\n'.format(title)

      template = advanced_manager_template if self.use_firebase else base_manager_template
      content = format(template, [{'name': 'generic_class', 'replace': generic_class},
                                                   {'name': 'table', 'replace': table},
                                                   {'name': 'firebase_child_name', 'replace': firebase_child_name},
                                                   {'name': 'columns', 'replace': columns},
                                                   {'name': 'column_array', 'replace': column_array},
                                                   {'name': 'query_create', 'replace': query_create},
                                                   {'name': 'parse_data_snapshot', 'replace': parse_data_snapshot},
                                                   {'name': 'parse_result_set', 'replace': parse_result_set},
                                                   {'name': 'item_to_params', 'replace': item_to_params},
                                                   ])

      file.write(content)

    with open(source_path + base_package + 'model/item/{}.java'.format(self.generic_class), 'w', encoding="utf8") as file:
      build_item(file)

    with open(source_path + base_package + 'model/persistent/{}Manager.java'.format(self.generic_class), 'w', encoding="utf8") as file:
      build_manager(file)
