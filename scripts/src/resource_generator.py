#!/usr/bin/env python3

import os

folder = './src/main/resources/'
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

def generate_resources(root_folder, requested_extension, class_name, output_file):
  output_file.write("    public static class " + class_name + " {\n\n")
  for dirname, dirs, files in os.walk(root_folder):
    for filename in files:
      filename_without_extension, extension = os.path.splitext(filename)
      if extension == ('.' + requested_extension):
        output_file.write('        ' + string.format(filename_without_extension.upper(), filename_without_extension))
  output_file.write("    }\n\n")


def generate_translate_keys(translate_file, output_file):
  output_file.write("    public static class Translate {\n\n")
  with open(translate_file, "r", encoding="utf8") as f:
    lines = f.readlines()
    for line in lines:
      if "=" not in line:
        continue
      key = line[:line.index("=")]
      output_file.write('        ' + string.format(key.upper()[4:], key))
  output_file.write("    }\n\n")

def generate_config_keys(config, output_file):
  output_file.write("    public static class Config {\n\n")
  for key in config:
    output_file.write('        ' + string.format(key.upper(), key))
  output_file.write("    }\n\n")

def generate_image_paths(folder, subfolder, spaces, builded_path, output_file):
  def write_space(count, f):
    f.write(" " * count)
  def write_class(space_prefix, class_name, f):
    write_space(space_prefix, f)
    f.write("public static class " + class_name.capitalize() + " {\n\n")
  def write_entry(space_prefix, entry_name, entry_value, f):
    write_space(space_prefix, f)
    entry_name = entry_name.upper().replace("-", "_")
    entry_value = entry_value.replace("\\", "/")
    f.write('public static final String {} = "{}";\n'.format(entry_name, entry_value))


  write_class(spaces, subfolder, output_file)
  current_path = os.path.join(folder, subfolder)
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
  write_space(spaces, output_file)
  output_file.write("}\n\n")


with open(output_path, "w", encoding="utf8") as file:
  file.write("package cz.stechy.drd;\n\n")
  file.write('@SuppressWarnings("unused")\n')
  file.write("public final class R {\n\n")

  write_key_pair_values(file)
  generate_resources(folder + "fxml", "fxml", "FXML", file)
  generate_translate_keys(folder + "lang/translate_cs_CZ.properties", file)
  generate_config_keys(configuration, file)
  generate_image_paths(folder, "images", 4, "/images", file)

  file.write("}\n")
