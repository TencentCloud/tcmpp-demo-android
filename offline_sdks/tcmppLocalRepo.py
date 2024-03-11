import os
import requests
from lxml import etree

def download_file(url, local_path):
    response = requests.get(url)
    response.raise_for_status()

    os.makedirs(os.path.dirname(local_path), exist_ok=True)
    with open(local_path, 'wb') as f:
        f.write(response.content)

def download_dependency(dependency, base_url, local_repo):
    group_id, artifact_id, version, ext= dependency
    group_path = group_id.replace('.', '/')
    artifact_base_url = f"{base_url}/{group_path}/{artifact_id}/{version}"
    local_repo_path = f"{local_repo}/{group_path}/{artifact_id}/{version}"

    jar_url = f"{artifact_base_url}/{artifact_id}-{version}.{ext}"
    jar_local_path = f"{local_repo_path}/{artifact_id}-{version}.{ext}"
    download_file(jar_url, jar_local_path)

    pom_url = f"{artifact_base_url}/{artifact_id}-{version}.pom"
    pom_local_path = f"{local_repo_path}/{artifact_id}-{version}.pom"
    download_file(pom_url, pom_local_path)

def main():
    base_url = "https://tmf-work-maven.pkg.coding.net/repository/tmf/android/"
    local_repo = "../offline_sdks/tcmpp-local-repo"

    dependencies = [
        ("com.tencent.tcmpp.android", "mini_annotation", "1.5.1", "jar"),
        ("com.tencent.tcmpp.android", "mini_annotation_processor", "1.5.2", "jar"),
        ("com.squareup", "javapoet", "1.11.1", "jar")
    ]

    for dependency in dependencies:
        download_dependency(dependency, base_url, local_repo)

if __name__ == "__main__":
    main()