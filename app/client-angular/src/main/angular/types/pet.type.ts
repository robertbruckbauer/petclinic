export interface Pet {
  id: string | undefined;
  version: number;
  name: string;
  born: string;
  species: string;
  owner: string;
}

export interface PetItem {
  value: string;
  text: string;
}
