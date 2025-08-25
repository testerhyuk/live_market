import axios from "axios";

const API_SERVER_HOST = "http://localhost:9008";

const memberPrefix = `${API_SERVER_HOST}/v1/members`;

export interface SignUpRequest {
  email: string;
  password: string;
  passwordConfirm: string;
  nickname: string;
  address: string;
  detailAddress: string;
}

export interface SignUpResponse {
  memberId: string;
  email: string;
  nickname: string;
  address: string;
  detailAddress: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  memberId: string;
  email: string;
  nickname: string;
  token: string;
  tokenType: string;
}

export const getMemberById = async (memberId: string) => {
  const token = localStorage.getItem("token");
  const res = await axios.get(`${memberPrefix}/info/${memberId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return res.data;
};

export const updateMember = async (memberId: string, data: any) => {
  const token = localStorage.getItem("token");
  const res = await axios.put(`${memberPrefix}/modify/${memberId}`, data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return res.data;
};

export const fetchNickname = async (memberId: string): Promise<string> => {
  try {
    const res = await axios.get(`${memberPrefix}/nickname/${memberId}`);
    
    return res.data;
  } catch (err) {
    console.error(`닉네임 조회 실패: ${memberId}`, err);
    return "알 수 없음";
  }
};

export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const res = await axios.post(`${memberPrefix}/login`, data, { withCredentials: true });

  return res.data;
};

export const signup = async (data: SignUpRequest) => {
  try {
    const response = await axios.post<SignUpResponse>(
      `${memberPrefix}/signup`,
      data
    );
    return response.data;
  } catch (err: any) {
    throw err.response?.data?.message || "회원가입 실패";
  }
};