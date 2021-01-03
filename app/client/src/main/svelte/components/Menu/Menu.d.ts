import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class Menu extends SvelteTypedComponent<MenuProps, MenuEvents, MenuSlots> {
}

declare const _MenuProps: {

    /** 
     * Show menu.
     */
    show?: boolean;

} & SvelteAllProps;

declare const _MenuEvents: {
};

declare const _MenuSlots: {
    default: {};
};

export declare type MenuProps = typeof _MenuProps;
export declare type MenuEvents = typeof _MenuEvents;
export declare type MenuSlots = typeof _MenuSlots;
export {
};
