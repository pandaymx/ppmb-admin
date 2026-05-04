export interface LoginParams {
  username: string;
  password?: string;
}

export interface LoginResult {
  accessToken: string;
  expiresIn: number;
}

export interface UserInfo {
  id: number;
  username: string;
  permissions: string[];
}
