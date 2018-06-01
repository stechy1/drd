#!/usr/bin/env python3

import os

folder = './drd_client//src/main/resources/'
string = 'public static final String {} = "{}";\n'
constant_builder = 'public static final {} {} = {};\n'
output_path = './drd_share/src/main/java/cz/stechy/drd/R.java'

constants = [
	{'name': 'DATABASE_VERSION', 'type': 'int', 'value': '1'}
]

configuration = [
    'offline_database_name', 'use_online_database', 'online_database_credentials_path', 'hero_levelup_points_per_level', 'database_version'
]

def write_key_pair_values(output_file):
  for constant in constants:
    output_file.write("    " + constant_builder.format(constant['type'], constant['name'], constant['value']))
  output_file.write("\n")

# pomocné funkce pro generování obsahu
def to_camel_case(snake_str):
  return ''.join(x.capitalize() or '_' for x in snake_str.split('_'))
def write_space(count, f):
  f.write(" " * count)
def write_class(space_prefix, class_name, f):
  write_space(space_prefix, f)
  class_name = to_camel_case(class_name)
  f.write("public static class " + class_name.capitalize() + " {\n\n")
def close_class(space_prefix, f):
  write_space(space_prefix, f)
  f.write("}\n\n")
def write_entry(space_prefix, entry_name, entry_value, f):
  write_space(space_prefix, f)
  entry_name = entry_name.upper().replace("-", "_")
  entry_value = entry_value.replace("\\", "/")
  f.write('public static final String {} = "{}";\n'.format(entry_name, entry_value))

def generate_resources(root_folder, requested_extension, class_name, output_file):
  write_class(4, class_name, output_file)
  for dirname, dirs, files in os.walk(root_folder):
    for filename in files:
      filename_without_extension, extension = os.path.splitext(filename)
      if extension == ('.' + requested_extension):
        output_file.write('        ' + string.format(filename_without_extension.upper(), filename_without_extension))
  close_class(4, output_file)


def generate_translate_keys(translate_file, output_file):
  write_class(4, "Translate", output_file)
  with open(translate_file, "r", encoding="utf8") as f:
    lines = f.readlines()
    for line in lines:
      if "=" not in line:
        continue
      key = line[:line.index("=")]
      write_entry(8, key[4:], key, output_file)
  close_class(4, output_file)

def generate_config_keys(config, output_file):
  write_class(4, "Config", output_file)
  for key in config:
    write_entry(8, key, key, output_file)
  close_class(4, output_file)

def generate_image_paths(root_folder, subfolder, spaces, builded_path, output_file):
  write_class(spaces, subfolder, output_file)
  current_path = os.path.join(root_folder, subfolder)
  for entry in os.listdir(current_path):

    entry_path = os.path.join(current_path, entry)
    is_dir = os.path.isdir(entry_path)
    if is_dir:
      generate_image_paths(current_path, entry, spaces + 4, os.path.join(builded_path, entry), output_file)
      continue

    if ".png" not in entry:
      continue

    dot_index = entry.index(".")
    entry_name = entry[:dot_index]
    write_entry(spaces + 4, entry_name.upper(), os.path.join(builded_path, entry), output_file)
  close_class(spaces, output_file)

def generate_table_columns(output_file):
  table_columns_file = "./scripts/resources/table_columns.txt"
  write_class(4, "Database", output_file)

  with open(table_columns_file, "r", encoding="utf8") as columns_file:
    columns = columns_file.read().splitlines()
  for column in columns:
    table_name = column[:column.find(":")]
    write_class(8, table_name, output_file)
    write_entry(12, "table_name", table_name, output_file)
    raw_columns = (column[column.find(":") + 2 :]).split(", ")
    for raw_column in raw_columns:
      write_entry(12, "COLUMN_" + raw_column, table_name + "_" + raw_column, output_file)

    close_class(8, output_file)
    print(raw_columns)

  close_class(4, output_file)

with open(output_path, "w", encoding="utf8") as file:
  file.write("package cz.stechy.drd;\n\n")
  file.write('@SuppressWarnings("unused")\n')
  file.write("public final class R {\n\n")

  write_key_pair_values(file)
  generate_resources(folder + "fxml", "fxml", "FXML", file)
  generate_translate_keys(folder + "lang/translate_cs_CZ.properties", file)
  generate_config_keys(configuration, file)
  generate_image_paths(folder, "images", 4, "/images", file)
  generate_table_columns(file)

  file.write("}\n")
