import axios from 'axios'
import type { Config } from '@/types/Config'
import UserService from '@/service/UserService'

export default class ConfigService {

  public static async getConfigsByUser() {
    const response = await axios
      .get<Config[]>('api/configs/user')
    return response.data;
  }

  public static async getConfigByKey(key: string) {
    try {
      const response = await axios.get<Config>('api/configs/' + key)
      return response.data.value
    } catch (reason: any) {
      if (reason.response!.status === 500) {
        return 'Not Found!'
      }
    }
  }

  public static  async deleteConfig(id: number) {
    return await axios.delete('api/configs/' + id)
  }

  public static async updateConfig(config: Config) {
    return await axios.put('api/configs', config)
  }

  public static async createConfig(config: Config) {
    return await axios.post('api/configs', config)
  }

  public static handleError(error: any){
    if(error.response.status === 500){
      UserService.handleNotAuthenticatedUser();
    }
    else{
      console.log(error);
    }
  }
}
