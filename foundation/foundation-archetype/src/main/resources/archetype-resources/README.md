# ${projectName}

## Deployment
This project leverages [Helmfile](https://helmfile.readthedocs.io/en/latest/) for deployment. Follow the below commands
to deploy or tear down the project/pipeline(s):

- Deploy all project applications except pipeline: `helmfile apply -f helmfile-apps.yaml`
- Deploy pipeline: `helmfile sync -l name=<pipeline-name>`
- Tear down pipeline: `helmfile destroy -l name=<pipeline-name>`
- Tear down project: `helmfile destroy`

Running the Project in a higher environment like CI can be done by changing the `--environment` option. First the 
helmfile's `kubeContext` needs to be updated to point to the appropriate Kubernetes context. Then running the same 
helmfile apply and destroy commands above will deploy the app